package com.blackwaterpragmatic.joggingtracker.resource;

import static com.blackwaterpragmatic.joggingtracker.constant.RequestScopeAttribute.AUTHENTICATED_USER;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.blackwaterpragmatic.joggingtracker.bean.Password;
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
import javax.ws.rs.core.Response;

import java.io.IOException;
import java.net.URISyntaxException;

@RunWith(MockitoJUnitRunner.class)
public class UserResourceTest {

	@Mock
	private UserService userService;

	@Mock
	private HttpServletRequest httpServletRequest;

	private UserResource userResource;

	@Before
	public void before() {
		// using ResponseHelper directly instead of mocking to verify actual response
		userResource = new UserResource(userService, new ResponseHelper());

		ResteasyProviderFactory.getContextDataMap().put(HttpServletRequest.class, httpServletRequest);
	}

	@Test
	public void should_get_user() throws URISyntaxException, IOException {
		final Dispatcher dispatcher = MockHelper.createMockDispatcher(userResource);

		final Long userId = 1L;
		final User user = new User() {
			{
				setId(userId);
			}
		};

		when(httpServletRequest.getAttribute(AUTHENTICATED_USER)).thenReturn(user);
		when(userService.getUser(userId)).thenReturn(user);

		final String expectedResponse = new ObjectMapper().writeValueAsString(user);

		final MockHttpRequest request = MockHttpRequest.get("/user");
		final MockHttpResponse response = new MockHttpResponse();

		dispatcher.invoke(request, response);

		verify(httpServletRequest).getAttribute(AUTHENTICATED_USER);
		verify(userService).getUser(userId);
		verifyNoMoreInteractions(MockHelper.allDeclaredMocks(this));

		assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
		assertEquals(expectedResponse, response.getContentAsString());
	}

	@Test
	public void should_not_get_missing_user() throws URISyntaxException, IOException {
		final Dispatcher dispatcher = MockHelper.createMockDispatcher(userResource);

		final Long userId = 1L;
		final User user = new User() {
			{
				setId(userId);
			}
		};
		final ResponseMessage error = new ResponseMessage("Authenticated user not found.  Reauthenticate.");

		when(httpServletRequest.getAttribute(AUTHENTICATED_USER)).thenReturn(user);
		when(userService.getUser(userId)).thenReturn(null);

		final String expectedResponse = new ObjectMapper().writeValueAsString(error);

		final MockHttpRequest request = MockHttpRequest.get("/user");
		final MockHttpResponse response = new MockHttpResponse();

		dispatcher.invoke(request, response);

		verify(httpServletRequest).getAttribute(AUTHENTICATED_USER);
		verify(userService).getUser(userId);
		verifyNoMoreInteractions(MockHelper.allDeclaredMocks(this));

		assertEquals(Response.Status.UNAUTHORIZED.getStatusCode(), response.getStatus());
		assertEquals(expectedResponse, response.getContentAsString());
	}

	@Test
	public void should_update_user() throws URISyntaxException, IOException {
		final Dispatcher dispatcher = MockHelper.createMockDispatcher(userResource);

		final Long userId = 1L;
		final User user = new User() {
			{
				setId(userId);
			}
		};

		when(httpServletRequest.getAttribute(AUTHENTICATED_USER)).thenReturn(user);
		when(userService.updateUser(any(User.class), eq(false))).thenReturn(user);

		final String requestBody = new ObjectMapper().writeValueAsString(user);
		final String expectedResponse = new ObjectMapper().writeValueAsString(user);

		final MockHttpRequest request = MockHttpRequest.put("/user")
				.contentType(MediaType.JSON)
				.content(requestBody.getBytes());
		final MockHttpResponse response = new MockHttpResponse();

		dispatcher.invoke(request, response);

		verify(httpServletRequest).getAttribute(AUTHENTICATED_USER);
		verify(userService).updateUser(any(User.class), eq(false));
		verifyNoMoreInteractions(MockHelper.allDeclaredMocks(this));

		assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
		assertEquals(expectedResponse, response.getContentAsString());
	}

	@Test
	public void should_delete_user() throws URISyntaxException, IOException {
		final Dispatcher dispatcher = MockHelper.createMockDispatcher(userResource);

		final Long userId = 1L;
		final User user = new User() {
			{
				setId(userId);
			}
		};

		when(httpServletRequest.getAttribute(AUTHENTICATED_USER)).thenReturn(user);

		final MockHttpRequest request = MockHttpRequest.delete("/user");
		final MockHttpResponse response = new MockHttpResponse();

		dispatcher.invoke(request, response);

		verify(httpServletRequest).getAttribute(AUTHENTICATED_USER);
		verify(userService).deactivate(userId);
		verifyNoMoreInteractions(MockHelper.allDeclaredMocks(this));

		assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
		assertTrue(response.getContentAsString().isEmpty());
	}

	@Test
	public void should_update_password() throws URISyntaxException, IOException {
		final Dispatcher dispatcher = MockHelper.createMockDispatcher(userResource);

		final Long userId = 1L;
		final User user = new User() {
			{
				setId(userId);
			}
		};
		final Password password = new Password() {
			{
				setPassword("password");
			}
		};

		when(httpServletRequest.getAttribute(AUTHENTICATED_USER)).thenReturn(user);

		final String requestBody = new ObjectMapper().writeValueAsString(password);

		final MockHttpRequest request = MockHttpRequest.put("/user/password")
				.contentType(MediaType.JSON)
				.content(requestBody.getBytes());
		final MockHttpResponse response = new MockHttpResponse();

		dispatcher.invoke(request, response);

		verify(httpServletRequest).getAttribute(AUTHENTICATED_USER);
		verify(userService).updatePassword(userId, "password");
		verifyNoMoreInteractions(MockHelper.allDeclaredMocks(this));

		assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
		assertTrue(response.getContentAsString().isEmpty());
	}
}
