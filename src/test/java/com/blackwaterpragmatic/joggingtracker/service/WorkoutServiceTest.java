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

}
