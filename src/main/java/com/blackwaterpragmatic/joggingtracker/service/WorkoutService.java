package com.blackwaterpragmatic.joggingtracker.service;

import com.blackwaterpragmatic.joggingtracker.bean.Weather;
import com.blackwaterpragmatic.joggingtracker.bean.Workout;
import com.blackwaterpragmatic.joggingtracker.bean.internal.ApplicationEnvironment;
import com.blackwaterpragmatic.joggingtracker.helper.ExternalWebServiceHelper;
import com.blackwaterpragmatic.joggingtracker.mybatis.mapper.WorkoutMapper;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URL;
import java.util.List;

@Service
public class WorkoutService {

	private final WorkoutMapper workoutMapper;
	private final ExternalWebServiceHelper webServiceHelper;
	private final String weatherUrlString;
	private final ObjectMapper jsonMapper;

	@Autowired
	public WorkoutService(
			final WorkoutMapper workoutMapper,
			final ExternalWebServiceHelper webServiceHelper,
			final ApplicationEnvironment applicationEnvironment) {
		this.workoutMapper = workoutMapper;
		this.webServiceHelper = webServiceHelper;
		weatherUrlString = applicationEnvironment.getWeatherUrl();
		jsonMapper = new ObjectMapper();
	}

	public List<Workout> getWorkouts(final Long userId, final Integer start, final Integer max) {
		return workoutMapper.list(userId, start, max);
	}

	public Workout getWorkout(final Long userId, final Long workoutId) {
		return workoutMapper.fetch(userId, workoutId);
	}

	public Workout addWorkout(final Workout workout) throws IOException {
		workout.setWeather(getWeather(workout.getDateMs(), workout.getPostalCode()));
		workoutMapper.insert(workout);
		return workout;
	}

	public Workout updateWorkout(final Workout workout) {
		workoutMapper.update(workout);
		return workout;
	}

	public void deleteWorkout(final Long userId, final Long workoutId) {
		workoutMapper.delete(userId, workoutId);
	}

	private String getWeather(final Long dateMs, final String postalCode) throws IOException {
		final URL weatherUrl = new URL(String.format("%s/weather?dateMs=%s&postalCode=%s",
				weatherUrlString,
				dateMs,
				postalCode));
		final String weatherResponse = webServiceHelper.get(weatherUrl);
		final Weather weather = parseWeatherResponse(weatherResponse);
		return (null == weather.getWeather() ? "UNKNOWN" : weather.getWeather());
	}

	private Weather parseWeatherResponse(final String weatherResponse) throws IOException {
		if (null == weatherResponse) {
			return new Weather();
		}
		return jsonMapper.readValue(weatherResponse, Weather.class);
	}

}
