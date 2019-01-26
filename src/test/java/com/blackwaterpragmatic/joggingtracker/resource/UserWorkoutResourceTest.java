package com.blackwaterpragmatic.joggingtracker.resource;

import static com.blackwaterpragmatic.joggingtracker.constant.RequestScopeAttribute.AUTHENTICATED_USER;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.blackwaterpragmatic.joggingtracker.bean.ResponseMessage;
import com.blackwaterpragmatic.joggingtracker.bean.User;
import com.blackwaterpragmatic.joggingtracker.bean.Workout;
import com.blackwaterpragmatic.joggingtracker.constant.MediaType;
import com.blackwaterpragmatic.joggingtracker.helper.ResponseHelper;
import com.blackwaterpragmatic.joggingtracker.service.WorkoutService;
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
public class UserWorkoutResourceTest {

	@Mock
	private WorkoutService workoutService;

	@Mock
	private HttpServletRequest httpServletRequest;

	private UserWorkoutResource userWorkoutResource;

	@Before
	public void before() {
		// using ResponseHelper directly instead of mocking to verify actual response
		userWorkoutResource = new UserWorkoutResource(workoutService, new ResponseHelper());

		ResteasyProviderFactory.getContextDataMap().put(HttpServletRequest.class, httpServletRequest);
	}

	@Test
	public void should_get_all_workouts() throws URISyntaxException, IOException {
		final Dispatcher dispatcher = MockHelper.createMockDispatcher(userWorkoutResource);

		final Long userId = 1L;
		final User user = new User() {
			{
				setId(userId);
			}
		};
		final List<Workout> workouts = new ArrayList<>();

		when(httpServletRequest.getAttribute(AUTHENTICATED_USER)).thenReturn(user);
		when(workoutService.getWorkouts(userId)).thenReturn(workouts);

		final String expectedResponse = new ObjectMapper().writeValueAsString(workouts);

		final MockHttpRequest request = MockHttpRequest.get("/user/workouts");
		final MockHttpResponse response = new MockHttpResponse();

		dispatcher.invoke(request, response);

		verify(httpServletRequest).getAttribute(AUTHENTICATED_USER);
		verify(workoutService).getWorkouts(userId);
		verifyNoMoreInteractions(MockHelper.allDeclaredMocks(this));

		assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
		assertEquals(expectedResponse, response.getContentAsString());
	}

	@Test
	public void should_get_workout() throws URISyntaxException, IOException {
		final Dispatcher dispatcher = MockHelper.createMockDispatcher(userWorkoutResource);

		final Long userId = 1L;
		final User user = new User() {
			{
				setId(userId);
			}
		};
		final Long workoutId = 2L;
		final Workout workout = new Workout() {
			{
				setId(workoutId);
			}
		};

		when(httpServletRequest.getAttribute(AUTHENTICATED_USER)).thenReturn(user);
		when(workoutService.getWorkout(userId, workoutId)).thenReturn(workout);

		final String expectedResponse = new ObjectMapper().writeValueAsString(workout);

		final MockHttpRequest request = MockHttpRequest.get("/user/workouts/2");
		final MockHttpResponse response = new MockHttpResponse();

		dispatcher.invoke(request, response);

		verify(httpServletRequest).getAttribute(AUTHENTICATED_USER);
		verify(workoutService).getWorkout(userId, workoutId);
		verifyNoMoreInteractions(MockHelper.allDeclaredMocks(this));

		assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
		assertEquals(expectedResponse, response.getContentAsString());
	}

	@Test
	public void should_not_get_missing_workout() throws URISyntaxException, IOException {
		final Dispatcher dispatcher = MockHelper.createMockDispatcher(userWorkoutResource);

		final Long userId = 1L;
		final User user = new User() {
			{
				setId(userId);
			}
		};
		final Long workoutId = 2L;
		final ResponseMessage error = new ResponseMessage("Workout not found.");

		when(httpServletRequest.getAttribute(AUTHENTICATED_USER)).thenReturn(user);
		when(workoutService.getWorkout(userId, workoutId)).thenReturn(null);

		final String expectedResponse = new ObjectMapper().writeValueAsString(error);

		final MockHttpRequest request = MockHttpRequest.get("/user/workouts/2");
		final MockHttpResponse response = new MockHttpResponse();

		dispatcher.invoke(request, response);

		verify(httpServletRequest).getAttribute(AUTHENTICATED_USER);
		verify(workoutService).getWorkout(userId, workoutId);
		verifyNoMoreInteractions(MockHelper.allDeclaredMocks(this));

		assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
		assertEquals(expectedResponse, response.getContentAsString());
	}

	@Test
	public void should_add_workout() throws URISyntaxException, IOException {
		final Dispatcher dispatcher = MockHelper.createMockDispatcher(userWorkoutResource);

		final Long userId = 1L;
		final User user = new User() {
			{
				setId(userId);
			}
		};
		final Long workoutId = 2L;
		final Workout newWorkout = new Workout();
		final Workout workout = new Workout() {
			{
				setId(workoutId);
			}
		};

		when(httpServletRequest.getAttribute(AUTHENTICATED_USER)).thenReturn(user);
		when(workoutService.addWorkout(any(Workout.class))).thenReturn(workout);

		final String requestBody = new ObjectMapper().writeValueAsString(newWorkout);
		final String expectedResponse = new ObjectMapper().writeValueAsString(workout);

		final MockHttpRequest request = MockHttpRequest.post("/user/workouts")
				.contentType(MediaType.JSON)
				.content(requestBody.getBytes());
		final MockHttpResponse response = new MockHttpResponse();

		dispatcher.invoke(request, response);

		verify(httpServletRequest).getAttribute(AUTHENTICATED_USER);
		verify(workoutService).addWorkout(any(Workout.class));
		verifyNoMoreInteractions(MockHelper.allDeclaredMocks(this));

		assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
		assertEquals("/user/workout/2", response.getOutputHeaders().get("Location").get(0).toString());
		assertEquals(expectedResponse, response.getContentAsString());
	}

	@Test
	public void should_update_workout() throws URISyntaxException, IOException {
		final Dispatcher dispatcher = MockHelper.createMockDispatcher(userWorkoutResource);

		final Long userId = 1L;
		final User user = new User() {
			{
				setId(userId);
			}
		};
		final Long workoutId = 2L;
		final Workout updatedWorkout = new Workout();
		final Workout workout = new Workout() {
			{
				setId(workoutId);
			}
		};

		when(httpServletRequest.getAttribute(AUTHENTICATED_USER)).thenReturn(user);
		when(workoutService.updateWorkout(any(Workout.class))).thenReturn(workout);

		final String requestBody = new ObjectMapper().writeValueAsString(updatedWorkout);
		final String expectedResponse = new ObjectMapper().writeValueAsString(workout);

		final MockHttpRequest request = MockHttpRequest.put("/user/workouts/2")
				.contentType(MediaType.JSON)
				.content(requestBody.getBytes());
		final MockHttpResponse response = new MockHttpResponse();

		dispatcher.invoke(request, response);

		verify(httpServletRequest).getAttribute(AUTHENTICATED_USER);
		verify(workoutService).updateWorkout(any(Workout.class));
		verifyNoMoreInteractions(MockHelper.allDeclaredMocks(this));

		assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
		assertEquals(expectedResponse, response.getContentAsString());
	}

	@Test
	public void should_not_update_workout_not_found() throws URISyntaxException, IOException {
		final Dispatcher dispatcher = MockHelper.createMockDispatcher(userWorkoutResource);

		final Long userId = 1L;
		final User user = new User() {
			{
				setId(userId);
			}
		};
		final Workout updatedWorkout = new Workout();
		final ResponseMessage error = new ResponseMessage("Workout not found.");

		when(httpServletRequest.getAttribute(AUTHENTICATED_USER)).thenReturn(user);
		when(workoutService.updateWorkout(any(Workout.class))).thenReturn(null);

		final String requestBody = new ObjectMapper().writeValueAsString(updatedWorkout);
		final String expectedResponse = new ObjectMapper().writeValueAsString(error);

		final MockHttpRequest request = MockHttpRequest.put("/user/workouts/2")
				.contentType(MediaType.JSON)
				.content(requestBody.getBytes());
		final MockHttpResponse response = new MockHttpResponse();

		dispatcher.invoke(request, response);

		verify(httpServletRequest).getAttribute(AUTHENTICATED_USER);
		verify(workoutService).updateWorkout(any(Workout.class));
		verifyNoMoreInteractions(MockHelper.allDeclaredMocks(this));

		assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
		assertEquals(expectedResponse, response.getContentAsString());
	}

	@Test
	public void should_delete_workout() throws URISyntaxException, IOException {
		final Dispatcher dispatcher = MockHelper.createMockDispatcher(userWorkoutResource);

		final Long userId = 1L;
		final User user = new User() {
			{
				setId(userId);
			}
		};
		final Long workoutId = 2L;

		when(httpServletRequest.getAttribute(AUTHENTICATED_USER)).thenReturn(user);

		final MockHttpRequest request = MockHttpRequest.delete("/user/workouts/2");
		final MockHttpResponse response = new MockHttpResponse();

		dispatcher.invoke(request, response);

		verify(httpServletRequest).getAttribute(AUTHENTICATED_USER);
		verify(workoutService).deleteWorkout(userId, workoutId);
		verifyNoMoreInteractions(MockHelper.allDeclaredMocks(this));

		assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
		assertTrue(response.getContentAsString().isEmpty());
	}

}
