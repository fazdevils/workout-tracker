package com.blackwaterpragmatic.joggingtracker.resource;

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
import java.util.ArrayList;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class UserManagementResourceTest {

	@Mock
	private UserService userService;

	@Mock
	private HttpServletRequest httpServletRequest;

	private UserManagementResource userManagementResource;

	@Before
	public void before() {
		// using ResponseHelper directly instead of mocking to verify actual response
		userManagementResource = new UserManagementResource(userService, new ResponseHelper());

		ResteasyProviderFactory.getContextDataMap().put(HttpServletRequest.class, httpServletRequest);
	}

	@Test
	public void should_list_users() throws URISyntaxException, IOException {
		final Dispatcher dispatcher = MockHelper.createMockDispatcher(userManagementResource);

		final List<User> users = new ArrayList<User>() {
			{
				add(new User());
			}
		};

		when(userService.listUsers(null, null)).thenReturn(users);

		final String expectedResponse = new ObjectMapper().writeValueAsString(users);

		final MockHttpRequest request = MockHttpRequest.get("/user-management/users");
		final MockHttpResponse response = new MockHttpResponse();

		dispatcher.invoke(request, response);

		verify(userService).listUsers(null, null);
		verifyNoMoreInteractions(MockHelper.allDeclaredMocks(this));

		assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
		assertEquals(expectedResponse, response.getContentAsString());
	}

	@Test
	public void should_get_user() throws URISyntaxException, IOException {
		final Dispatcher dispatcher = MockHelper.createMockDispatcher(userManagementResource);

		final Long id = 1L;

		final User user = new User() {
			{
				setId(id);
			}
		};

		when(userService.getUser(id)).thenReturn(user);

		final String expectedResponse = new ObjectMapper().writeValueAsString(user);

		final MockHttpRequest request = MockHttpRequest.get("/user-management/users/" + id);
		final MockHttpResponse response = new MockHttpResponse();

		dispatcher.invoke(request, response);

		verify(userService).getUser(id);
		verifyNoMoreInteractions(MockHelper.allDeclaredMocks(this));

		assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
		assertEquals(expectedResponse, response.getContentAsString());
	}

	@Test
	public void should_not_get_missing_user() throws URISyntaxException, IOException {
		final Dispatcher dispatcher = MockHelper.createMockDispatcher(userManagementResource);

		final Long id = 1L;

		when(userService.getUser(id)).thenReturn(null);

		final String expectedResponse = new ObjectMapper().writeValueAsString(new ResponseMessage("User not found."));

		final MockHttpRequest request = MockHttpRequest.get("/user-management/users/" + id);
		final MockHttpResponse response = new MockHttpResponse();

		dispatcher.invoke(request, response);

		verify(userService).getUser(id);
		verifyNoMoreInteractions(MockHelper.allDeclaredMocks(this));

		assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
		assertEquals(expectedResponse, response.getContentAsString());
	}

	@Test
	public void should_update_user() throws URISyntaxException, IOException {
		final Dispatcher dispatcher = MockHelper.createMockDispatcher(userManagementResource);

		final Long userId = 1L;
		final User user = new User() {
			{
				setId(userId);
			}
		};

		when(userService.updateUser(any(User.class), eq(true))).thenReturn(user);

		final String requestBody = new ObjectMapper().writeValueAsString(user);
		final String expectedResponse = new ObjectMapper().writeValueAsString(user);

		final MockHttpRequest request = MockHttpRequest.put("/user-management/users/" + userId)
				.contentType(MediaType.JSON)
				.content(requestBody.getBytes());
		final MockHttpResponse response = new MockHttpResponse();

		dispatcher.invoke(request, response);

		verify(userService).updateUser(any(User.class), eq(true));
		verifyNoMoreInteractions(MockHelper.allDeclaredMocks(this));

		assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
		assertEquals(expectedResponse, response.getContentAsString());
	}

	@Test
	public void should_not_update_missing_user() throws URISyntaxException, IOException {
		final Dispatcher dispatcher = MockHelper.createMockDispatcher(userManagementResource);

		final Long userId = 1L;
		final User user = new User() {
			{
				setId(userId);
			}
		};

		when(userService.updateUser(any(User.class), eq(true))).thenReturn(null);

		final String requestBody = new ObjectMapper().writeValueAsString(user);
		final String expectedResponse = new ObjectMapper().writeValueAsString(new ResponseMessage("User not found."));

		final MockHttpRequest request = MockHttpRequest.put("/user-management/users/" + userId)
				.contentType(MediaType.JSON)
				.content(requestBody.getBytes());
		final MockHttpResponse response = new MockHttpResponse();

		dispatcher.invoke(request, response);

		verify(userService).updateUser(any(User.class), eq(true));
		verifyNoMoreInteractions(MockHelper.allDeclaredMocks(this));

		assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
		assertEquals(expectedResponse, response.getContentAsString());
	}

	@Test
	public void should_deactivate_user() throws URISyntaxException, IOException {
		final Dispatcher dispatcher = MockHelper.createMockDispatcher(userManagementResource);

		final Long userId = 1L;

		final MockHttpRequest request = MockHttpRequest.post("/user-management/users/1/deactivate")
				.contentType(MediaType.JSON);
		final MockHttpResponse response = new MockHttpResponse();

		dispatcher.invoke(request, response);

		verify(userService).deactivate(userId);
		verifyNoMoreInteractions(MockHelper.allDeclaredMocks(this));

		assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
		assertTrue(response.getContentAsString().isEmpty());
	}

	@Test
	public void should_activate_user() throws URISyntaxException, IOException {
		final Dispatcher dispatcher = MockHelper.createMockDispatcher(userManagementResource);

		final Long userId = 1L;

		final MockHttpRequest request = MockHttpRequest.post("/user-management/users/1/activate")
				.contentType(MediaType.JSON);
		final MockHttpResponse response = new MockHttpResponse();

		dispatcher.invoke(request, response);

		verify(userService).activate(userId);
		verifyNoMoreInteractions(MockHelper.allDeclaredMocks(this));

		assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
		assertTrue(response.getContentAsString().isEmpty());
	}

	@Test
	public void should_update_password() throws URISyntaxException, IOException {
		final Dispatcher dispatcher = MockHelper.createMockDispatcher(userManagementResource);

		final Long userId = 1L;
		final Password password = new Password() {
			{
				setPassword("password");
			}
		};

		final String requestBody = new ObjectMapper().writeValueAsString(password);

		final MockHttpRequest request = MockHttpRequest.put("/user-management/users/1/password")
				.contentType(MediaType.JSON)
				.content(requestBody.getBytes());
		final MockHttpResponse response = new MockHttpResponse();

		dispatcher.invoke(request, response);

		verify(userService).updatePassword(userId, "password");
		verifyNoMoreInteractions(MockHelper.allDeclaredMocks(this));

		assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
		assertTrue(response.getContentAsString().isEmpty());
	}

}
