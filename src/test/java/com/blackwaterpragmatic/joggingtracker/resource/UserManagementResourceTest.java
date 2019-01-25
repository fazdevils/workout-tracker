package com.blackwaterpragmatic.joggingtracker.resource;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.blackwaterpragmatic.joggingtracker.bean.ResponseMessage;
import com.blackwaterpragmatic.joggingtracker.bean.User;
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

		when(userService.listUsers()).thenReturn(users);

		final String expectedResponse = new ObjectMapper().writeValueAsString(users);

		final MockHttpRequest request = MockHttpRequest.get("/user-management/users");
		final MockHttpResponse response = new MockHttpResponse();

		dispatcher.invoke(request, response);

		verify(userService).listUsers();
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
	public void should_return_404_for_missing_user() throws URISyntaxException, IOException {
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

}
