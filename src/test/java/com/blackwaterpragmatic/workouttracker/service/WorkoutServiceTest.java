package com.blackwaterpragmatic.workouttracker.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.blackwaterpragmatic.workouttracker.bean.Weather;
import com.blackwaterpragmatic.workouttracker.bean.Workout;
import com.blackwaterpragmatic.workouttracker.bean.WorkoutReport;
import com.blackwaterpragmatic.workouttracker.bean.internal.ApplicationEnvironment;
import com.blackwaterpragmatic.workouttracker.helper.ExternalWebServiceHelper;
import com.blackwaterpragmatic.workouttracker.mybatis.mapper.WorkoutMapper;
import com.blackwaterpragmatic.workouttracker.service.WorkoutService;
import com.blackwaterpragmatic.workouttracker.test.MockHelper;
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
import java.util.Calendar;
import java.util.GregorianCalendar;
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

		when(workoutMapper.list(userId, null, null, null)).thenReturn(workouts);

		final List<Workout> allWorkouts = workoutService.getWorkouts(userId, null, null, null);

		verify(workoutMapper).list(userId, null, null, null);
		verifyNoMoreInteractions(MockHelper.allDeclaredMocks(this));

		assertEquals(workouts, allWorkouts);
	}

	@Test
	public void should_get_filtered_workouts() {
		final Long userId = 1L;
		final List<Workout> workouts = new ArrayList<>();

		when(workoutMapper.list(userId, null, null,
				"(dateMs = 1) AND ((distance > 20) OR (distance < 10)) AND (duration != 2)")).thenReturn(workouts);

		final List<Workout> allWorkouts = workoutService.getWorkouts(userId, null, null,
				"(dateMs eq 1) AND ((distance gt 20) OR (distance lt 10)) AND (duration ne 2)");

		verify(workoutMapper).list(userId, null, null,
				"(dateMs = 1) AND ((distance > 20) OR (distance < 10)) AND (duration != 2)");
		verifyNoMoreInteractions(MockHelper.allDeclaredMocks(this));

		assertEquals(workouts, allWorkouts);
	}

	@Test
	public void should_get_filtered_workouts_with_sql_issue() {
		final Long userId = 1L;
		final List<Workout> workouts = new ArrayList<>();

		when(workoutMapper.list(userId, null, null, "\\\\ ''")).thenReturn(workouts);

		final List<Workout> allWorkouts = workoutService.getWorkouts(userId, null, null, "\\ '");

		verify(workoutMapper).list(userId, null, null, "\\\\ ''");
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

	@Test
	public void should_create_workout_report() {
		final Long userId = 1L;
		final Calendar workoutDate = new GregorianCalendar(2015, Calendar.MARCH, 14);
		final List<Workout> workouts = new ArrayList<Workout>() {
			{
				add(new Workout() {
					{
						setId(100L);
						setUserId(userId);
						setDateMs(workoutDate.getTimeInMillis());
						setPostalCode("postalCode");
						setDistance(10.0);
						setDuration(5.0);
					}
				});
				workoutDate.add(Calendar.DATE, 14);
				add(new Workout() {
					{
						setId(100L);
						setUserId(userId);
						setDateMs(workoutDate.getTimeInMillis());
						setPostalCode("postalCode");
						setDistance(11.0);
						setDuration(6.0);
					}
				});
				workoutDate.add(Calendar.DATE, -15);
				add(new Workout() {
					{
						setId(100L);
						setUserId(userId);
						setDateMs(workoutDate.getTimeInMillis());
						setPostalCode("postalCode2");
						setDistance(13.0);
						setDuration(8.0);
					}
				});
			}
		};

		when(workoutMapper.list(userId, null, null, null)).thenReturn(workouts);

		final List<WorkoutReport> workoutReport = workoutService.createReport(userId);

		verify(workoutMapper).list(userId, null, null, null);
		verifyNoMoreInteractions(MockHelper.allDeclaredMocks(this));

		assertEquals(2, workoutReport.size());

		assertEquals(userId, workoutReport.get(0).getUserId());
		assertEquals(2015, workoutReport.get(0).getYear().intValue());
		assertEquals(11, workoutReport.get(0).getWeek().intValue());
		assertEquals(2, workoutReport.get(0).getWorkouts().intValue());
		assertEquals(23.0, workoutReport.get(0).getTotalDistance().doubleValue(), 0.01);
		assertEquals(13.0, workoutReport.get(0).getTotalDuration().doubleValue(), 0.01);
		assertEquals(11.5, workoutReport.get(0).getAverageDistance().doubleValue(), 0.01);
		assertEquals(6.5, workoutReport.get(0).getAverageDuration().doubleValue(), 0.01);

		assertEquals(userId, workoutReport.get(1).getUserId());
		assertEquals(2015, workoutReport.get(1).getYear().intValue());
		assertEquals(13, workoutReport.get(1).getWeek().intValue());
		assertEquals(1, workoutReport.get(1).getWorkouts().intValue());
		assertEquals(11.0, workoutReport.get(1).getTotalDistance().doubleValue(), 0.01);
		assertEquals(6.0, workoutReport.get(1).getTotalDuration().doubleValue(), 0.01);
		assertEquals(11.0, workoutReport.get(1).getAverageDistance().doubleValue(), 0.01);
		assertEquals(6.0, workoutReport.get(1).getAverageDuration().doubleValue(), 0.01);
	}

}
