package com.blackwaterpragmatic.joggingtracker.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.blackwaterpragmatic.joggingtracker.bean.Weather;
import com.blackwaterpragmatic.joggingtracker.bean.Workout;
import com.blackwaterpragmatic.joggingtracker.bean.internal.ApplicationEnvironment;
import com.blackwaterpragmatic.joggingtracker.helper.ExternalWebServiceHelper;
import com.blackwaterpragmatic.joggingtracker.mybatis.mapper.WorkoutMapper;
import com.blackwaterpragmatic.joggingtracker.test.MockHelper;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class WorkoutServiceTest {

	@Mock
	private WorkoutMapper workoutMapper;

	@Mock
	private ExternalWebServiceHelper webServiceHelper;

	@Mock
	private ApplicationEnvironment applicationEnvironment;

	private WorkoutService workoutService;

	@Before
	public void setup() {
		when(applicationEnvironment.getWeatherUrl()).thenReturn("http://localhost:8080");

		workoutService = new WorkoutService(
				workoutMapper,
				webServiceHelper,
				applicationEnvironment);

		verify(applicationEnvironment).getWeatherUrl();
		verifyNoMoreInteractions(MockHelper.allDeclaredMocks(this));
	}

	@Test
	public void should_get_workouts() {
		final Long userId = 1L;
		final List<Workout> workouts = new ArrayList<>();

		when(workoutMapper.list(userId, null, null)).thenReturn(workouts);

		final List<Workout> allWorkouts = workoutService.getWorkouts(userId, null, null);

		verify(workoutMapper).list(userId, null, null);
		verifyNoMoreInteractions(MockHelper.allDeclaredMocks(this));

		assertEquals(workouts, allWorkouts);
	}

	@Test
	public void should_get_workout() {
		final Long userId = 1L;
		final Long workoutId = 2L;
		final Workout workout = new Workout();

		when(workoutMapper.fetch(userId, workoutId)).thenReturn(workout);

		final Workout fetchedWorkout = workoutService.getWorkout(userId, workoutId);

		verify(workoutMapper).fetch(userId, workoutId);
		verifyNoMoreInteractions(MockHelper.allDeclaredMocks(this));

		assertEquals(workout, fetchedWorkout);
	}

	@Test
	public void should_add_workout() throws IOException {
		final Workout newWorkout = new Workout() {
			{
				setDateMs(1L);
				setPostalCode("postalCode");
			}
		};

		final Weather weather = new Weather() {
			{
				setWeather("weather");
			}
		};

		final String expectedWeatherResponse = new ObjectMapper().writeValueAsString(weather);

		when(webServiceHelper.get(any(URL.class))).thenReturn(expectedWeatherResponse);

		final Workout workout = workoutService.addWorkout(newWorkout);

		final ArgumentCaptor<URL> urlArgument = ArgumentCaptor.forClass(URL.class);
		verify(webServiceHelper).get(urlArgument.capture());
		verify(workoutMapper).insert(newWorkout);
		verifyNoMoreInteractions(MockHelper.allDeclaredMocks(this));

		assertEquals(newWorkout, workout);
		assertEquals("weather", workout.getWeather());
		assertEquals("http://localhost:8080/weather?dateMs=1&postalCode=postalCode", urlArgument.getValue().toString());
	}

	@Test
	public void should_add_workout_with_unknown_weather() throws IOException {
		final Workout newWorkout = new Workout() {
			{
				setDateMs(1L);
				setPostalCode("postalCode");
			}
		};

		when(webServiceHelper.get(any(URL.class))).thenReturn(null);

		final Workout workout = workoutService.addWorkout(newWorkout);

		final ArgumentCaptor<URL> urlArgument = ArgumentCaptor.forClass(URL.class);
		verify(webServiceHelper).get(urlArgument.capture());
		verify(workoutMapper).insert(newWorkout);
		verifyNoMoreInteractions(MockHelper.allDeclaredMocks(this));

		assertEquals(newWorkout, workout);
		assertEquals("UNKNOWN", workout.getWeather());
		assertEquals("http://localhost:8080/weather?dateMs=1&postalCode=postalCode", urlArgument.getValue().toString());
	}

	@Test
	public void should_update_workout() {
		final Workout workout = new Workout();

		final Workout updatedWorkout = workoutService.updateWorkout(workout);

		verify(workoutMapper).update(workout);
		verifyNoMoreInteractions(MockHelper.allDeclaredMocks(this));

		assertEquals(workout, updatedWorkout);
	}

	@Test
	public void should_delete_workout() {
		final Long userId = 1L;
		final Long workoutId = 2L;

		workoutService.deleteWorkout(userId, workoutId);

		verify(workoutMapper).delete(userId, workoutId);
		verifyNoMoreInteractions(MockHelper.allDeclaredMocks(this));
	}

}
