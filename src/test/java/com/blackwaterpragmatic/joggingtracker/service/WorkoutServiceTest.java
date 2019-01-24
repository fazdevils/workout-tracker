package com.blackwaterpragmatic.joggingtracker.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.blackwaterpragmatic.joggingtracker.bean.Workout;
import com.blackwaterpragmatic.joggingtracker.bean.internal.ApplicationEnvironment;
import com.blackwaterpragmatic.joggingtracker.helper.ExternalWebServiceHelper;
import com.blackwaterpragmatic.joggingtracker.mybatis.mapper.WorkoutMapper;
import com.blackwaterpragmatic.joggingtracker.test.MockHelper;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;

@RunWith(MockitoJUnitRunner.class)
public class WorkoutServiceTest {

	@Mock
	private WorkoutMapper workoutMapper;

	@Mock
	private ApplicationEnvironment applicationEnvironment;

	private WorkoutService workoutService;

	@Before
	public void setup() {
		when(applicationEnvironment.getWeatherUrl()).thenReturn("http://localhost:8080");

		workoutService = new WorkoutService(
				workoutMapper,
				new ExternalWebServiceHelper(), // TODO mock this
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


		final Workout workout = workoutService.addWorkout(newWorkout);

		verify(workoutMapper).insert(newWorkout);
		verifyNoMoreInteractions(MockHelper.allDeclaredMocks(this));

		assertEquals(newWorkout, workout);
	}

}