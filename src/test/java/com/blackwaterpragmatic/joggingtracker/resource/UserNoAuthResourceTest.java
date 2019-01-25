package com.blackwaterpragmatic.joggingtracker.resource;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.blackwaterpragmatic.joggingtracker.bean.AuthenticationToken;
import com.blackwaterpragmatic.joggingtracker.bean.Credentials;
import com.blackwaterpragmatic.joggingtracker.bean.NewUser;
import com.blackwaterpragmatic.joggingtracker.bean.ResponseMessage;
import com.blackwaterpragmatic.joggingtracker.bean.User;
import com.blackwaterpragmatic.joggingtracker.constant.MediaType;
import com.blackwaterpragmatic.joggingtracker.helper.ResponseHelper;
import com.blackwaterpragmatic.joggingtracker.service.UserService;
import com.blackwaterpragmatic.joggingtracker.test.MockHelper;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.mock.MockHttpRequest;
import org.jboss.resteasy.mock.MockHttpResponse;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;

import java.io.IOException;
import java.net.URISyntaxException;

@RunWith(MockitoJUnitRunner.class)
public class UserNoAuthResourceTest {

	@Mock
	private UserService userService;

	@Mock
	private HttpServletRequest httpServletRequest;

	private UserNoAuthResource userNoAuthResource;

	@Before
	public void before() {
		// using ResponseHelper directly instead of mocking to verify actual response
		userNoAuthResource = new UserNoAuthResource(userService, new ResponseHelper());

		ResteasyProviderFactory.getContextDataMap().put(HttpServletRequest.class, httpServletRequest);
	}

	@Test
	public void should_register_user() throws URISyntaxException, IOException {
		final Dispatcher dispatcher = MockHelper.createMockDispatcher(userNoAuthResource);

		final NewUser newUser = new NewUser();
		final User user = new User() {
			{
				setId(1L);
			}
		};

		when(userService.registerUser(any(NewUser.class), eq(false))).thenReturn(user);

		final String requestBody = new ObjectMapper().writeValueAsString(newUser);
		final String expectedResponse = new ObjectMapper().writeValueAsString(user);

		final MockHttpRequest request = MockHttpRequest.post("/user/register")
				.contentType(MediaType.JSON)
				.content(requestBody.getBytes());
		final MockHttpResponse response = new MockHttpResponse();

		dispatcher.invoke(request, response);

		verify(userService).registerUser(any(NewUser.class), eq(false));
		verifyNoMoreInteractions(MockHelper.allDeclaredMocks(this));

		assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
		assertEquals("/user", response.getOutputHeaders().get("Location").get(0).toString());
		assertEquals(expectedResponse, response.getContentAsString());
	}

	@Test
	public void should_authenticate() throws URISyntaxException, IOException {
		final Dispatcher dispatcher = MockHelper.createMockDispatcher(userNoAuthResource);

		final Credentials credentials = new Credentials();
		final String authenticationToken = "authenticationToken";
		final String userAgent = "userAgent";
		final AuthenticationToken token = new AuthenticationToken(authenticationToken);

		when(userService.buildAuthenticationToken(any(Credentials.class), eq(userAgent), any(HttpServletRequest.class))).thenReturn(authenticationToken);

		final String requestBody = new ObjectMapper().writeValueAsString(credentials);
		final String expectedResponse = new ObjectMapper().writeValueAsString(token);

		final MockHttpRequest request = MockHttpRequest.post("/user/authenticate")
				.header(HttpHeaders.USER_AGENT, userAgent)
				.contentType(MediaType.JSON)
				.content(requestBody.getBytes());
		final MockHttpResponse response = new MockHttpResponse();

		dispatcher.invoke(request, response);

		verify(userService).buildAuthenticationToken(any(Credentials.class), eq(userAgent), any(HttpServletRequest.class));
		verifyNoMoreInteractions(MockHelper.allDeclaredMocks(this));

		assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
		assertEquals(expectedResponse, response.getContentAsString());
	}

	@Test
	public void should_not_authenticate_invalid_credentials() throws URISyntaxException, IOException {
		final Dispatcher dispatcher = MockHelper.createMockDispatcher(userNoAuthResource);

		final Credentials credentials = new Credentials();
		final String userAgent = "userAgent";
		final ResponseMessage error = new ResponseMessage("Invalid credentials.");

		when(userService.buildAuthenticationToken(any(Credentials.class), eq(userAgent), any(HttpServletRequest.class))).thenReturn(null);

		final String requestBody = new ObjectMapper().writeValueAsString(credentials);
		final String expectedResponse = new ObjectMapper().writeValueAsString(error);

		final MockHttpRequest request = MockHttpRequest.post("/user/authenticate")
				.header(HttpHeaders.USER_AGENT, userAgent)
				.contentType(MediaType.JSON)
				.content(requestBody.getBytes());
		final MockHttpResponse response = new MockHttpResponse();

		dispatcher.invoke(request, response);

		verify(userService).buildAuthenticationToken(any(Credentials.class), eq(userAgent), any(HttpServletRequest.class));
		verifyNoMoreInteractions(MockHelper.allDeclaredMocks(this));

		assertEquals(Response.Status.UNAUTHORIZED.getStatusCode(), response.getStatus());
		assertEquals(expectedResponse, response.getContentAsString());
	}
}
