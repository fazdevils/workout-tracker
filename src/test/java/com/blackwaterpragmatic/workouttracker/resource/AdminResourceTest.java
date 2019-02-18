package com.blackwaterpragmatic.workouttracker.resource;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.blackwaterpragmatic.workouttracker.bean.ResponseMessage;
import com.blackwaterpragmatic.workouttracker.bean.Workout;
import com.blackwaterpragmatic.workouttracker.constant.MediaType;
import com.blackwaterpragmatic.workouttracker.helper.ResponseHelper;
import com.blackwaterpragmatic.workouttracker.resource.AdminResource;
import com.blackwaterpragmatic.workouttracker.service.UserService;
import com.blackwaterpragmatic.workouttracker.service.WorkoutService;
import com.blackwaterpragmatic.workouttracker.test.MockHelper;
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
public class AdminResourceTest {

	@Mock
	private UserService userService;

	@Mock
	private WorkoutService workoutService;

	@Mock
	private HttpServletRequest httpServletRequest;

	private AdminResource adminResource;

	@Before
	public void before() {
		// using ResponseHelper directly instead of mocking to verify actual response
		adminResource = new AdminResource(userService, workoutService, new ResponseHelper());

		ResteasyProviderFactory.getContextDataMap().put(HttpServletRequest.class, httpServletRequest);
	}

	@Test
	public void should_delete_user() throws URISyntaxException, IOException {
		final Dispatcher dispatcher = MockHelper.createMockDispatcher(adminResource);

		final Long userId = 1L;

		final MockHttpRequest request = MockHttpRequest.delete("/admin/users/1");
		final MockHttpResponse response = new MockHttpResponse();

		dispatcher.invoke(request, response);

		verify(userService).delete(userId);
		verifyNoMoreInteractions(MockHelper.allDeclaredMocks(this));

		assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
		assertTrue(response.getContentAsString().isEmpty());
	}

	@Test
	public void should_get_all_workouts() throws URISyntaxException, IOException {
		final Dispatcher dispatcher = MockHelper.createMockDispatcher(adminResource);

		final Long userId = 1L;
		final List<Workout> workouts = new ArrayList<>();

		when(workoutService.getWorkouts(userId, null, null, null)).thenReturn(workouts);

		final String expectedResponse = new ObjectMapper().writeValueAsString(workouts);

		final MockHttpRequest request = MockHttpRequest.get("/admin/users/1/workouts");
		final MockHttpResponse response = new MockHttpResponse();

		dispatcher.invoke(request, response);

		verify(workoutService).getWorkouts(userId, null, null, null);
		verifyNoMoreInteractions(MockHelper.allDeclaredMocks(this));

		assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
		assertEquals(expectedResponse, response.getContentAsString());
	}

	@Test
	public void should_get_workout() throws URISyntaxException, IOException {
		final Dispatcher dispatcher = MockHelper.createMockDispatcher(adminResource);

		final Long userId = 1L;
		final Long workoutId = 2L;
		final Workout workout = new Workout() {
			{
				setId(workoutId);
			}
		};

		when(workoutService.getWorkout(userId, workoutId)).thenReturn(workout);

		final String expectedResponse = new ObjectMapper().writeValueAsString(workout);

		final MockHttpRequest request = MockHttpRequest.get("/admin/users/1/workouts/2");
		final MockHttpResponse response = new MockHttpResponse();

		dispatcher.invoke(request, response);

		verify(workoutService).getWorkout(userId, workoutId);
		verifyNoMoreInteractions(MockHelper.allDeclaredMocks(this));

		assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
		assertEquals(expectedResponse, response.getContentAsString());
	}

	@Test
	public void should_not_get_missing_workout() throws URISyntaxException, IOException {
		final Dispatcher dispatcher = MockHelper.createMockDispatcher(adminResource);

		final Long userId = 1L;
		final Long workoutId = 2L;
		final ResponseMessage error = new ResponseMessage("Workout not found.");

		when(workoutService.getWorkout(userId, workoutId)).thenReturn(null);

		final String expectedResponse = new ObjectMapper().writeValueAsString(error);

		final MockHttpRequest request = MockHttpRequest.get("/admin/users/1/workouts/2");
		final MockHttpResponse response = new MockHttpResponse();

		dispatcher.invoke(request, response);

		verify(workoutService).getWorkout(userId, workoutId);
		verifyNoMoreInteractions(MockHelper.allDeclaredMocks(this));

		assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
		assertEquals(expectedResponse, response.getContentAsString());
	}

	@Test
	public void should_add_workout() throws URISyntaxException, IOException {
		final Dispatcher dispatcher = MockHelper.createMockDispatcher(adminResource);

		final Long userId = 1L;
		final Long workoutId = 2L;
		final Workout newWorkout = new Workout();
		final Workout workout = new Workout() {
			{
				setId(workoutId);
				setUserId(userId);
			}
		};

		when(workoutService.addWorkout(any(Workout.class))).thenReturn(workout);

		final String requestBody = new ObjectMapper().writeValueAsString(newWorkout);
		final String expectedResponse = new ObjectMapper().writeValueAsString(workout);

		final MockHttpRequest request = MockHttpRequest.post("/admin/users/1/workouts")
				.contentType(MediaType.JSON)
				.content(requestBody.getBytes());
		final MockHttpResponse response = new MockHttpResponse();

		dispatcher.invoke(request, response);

		verify(workoutService).addWorkout(any(Workout.class));
		verifyNoMoreInteractions(MockHelper.allDeclaredMocks(this));

		assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
		assertEquals("/user/workout/2", response.getOutputHeaders().get("Location").get(0).toString());
		assertEquals(expectedResponse, response.getContentAsString());
	}

	@Test
	public void should_update_workout() throws URISyntaxException, IOException {
		final Dispatcher dispatcher = MockHelper.createMockDispatcher(adminResource);

		final Long userId = 1L;
		final Long workoutId = 2L;
		final Workout updatedWorkout = new Workout();
		final Workout workout = new Workout() {
			{
				setId(workoutId);
				setUserId(userId);
			}
		};

		when(workoutService.updateWorkout(any(Workout.class))).thenReturn(workout);

		final String requestBody = new ObjectMapper().writeValueAsString(updatedWorkout);
		final String expectedResponse = new ObjectMapper().writeValueAsString(workout);

		final MockHttpRequest request = MockHttpRequest.put("/admin/users/1/workouts/2")
				.contentType(MediaType.JSON)
				.content(requestBody.getBytes());
		final MockHttpResponse response = new MockHttpResponse();

		dispatcher.invoke(request, response);

		verify(workoutService).updateWorkout(any(Workout.class));
		verifyNoMoreInteractions(MockHelper.allDeclaredMocks(this));

		assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
		assertEquals(expectedResponse, response.getContentAsString());
	}

	@Test
	public void should_not_update_workout_not_found() throws URISyntaxException, IOException {
		final Dispatcher dispatcher = MockHelper.createMockDispatcher(adminResource);

		final Workout updatedWorkout = new Workout();
		final ResponseMessage error = new ResponseMessage("Workout not found.");

		when(workoutService.updateWorkout(any(Workout.class))).thenReturn(null);

		final String requestBody = new ObjectMapper().writeValueAsString(updatedWorkout);
		final String expectedResponse = new ObjectMapper().writeValueAsString(error);

		final MockHttpRequest request = MockHttpRequest.put("/admin/users/1/workouts/2")
				.contentType(MediaType.JSON)
				.content(requestBody.getBytes());
		final MockHttpResponse response = new MockHttpResponse();

		dispatcher.invoke(request, response);

		verify(workoutService).updateWorkout(any(Workout.class));
		verifyNoMoreInteractions(MockHelper.allDeclaredMocks(this));

		assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
		assertEquals(expectedResponse, response.getContentAsString());
	}

	@Test
	public void should_delete_workout() throws URISyntaxException, IOException {
		final Dispatcher dispatcher = MockHelper.createMockDispatcher(adminResource);

		final Long userId = 1L;
		final Long workoutId = 2L;

		final MockHttpRequest request = MockHttpRequest.delete("/admin/users/1/workouts/2");
		final MockHttpResponse response = new MockHttpResponse();

		dispatcher.invoke(request, response);

		verify(workoutService).deleteWorkout(userId, workoutId);
		verifyNoMoreInteractions(MockHelper.allDeclaredMocks(this));

		assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
		assertTrue(response.getContentAsString().isEmpty());
	}

}
