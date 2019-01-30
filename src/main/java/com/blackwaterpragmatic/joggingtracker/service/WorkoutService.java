package com.blackwaterpragmatic.joggingtracker.service;

import com.blackwaterpragmatic.joggingtracker.bean.Weather;
import com.blackwaterpragmatic.joggingtracker.bean.Workout;
import com.blackwaterpragmatic.joggingtracker.bean.WorkoutReport;
import com.blackwaterpragmatic.joggingtracker.bean.internal.ApplicationEnvironment;
import com.blackwaterpragmatic.joggingtracker.helper.ExternalWebServiceHelper;
import com.blackwaterpragmatic.joggingtracker.mybatis.mapper.WorkoutMapper;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

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

	public List<Workout> getWorkouts(final Long userId, final Integer start, final Integer max, final String filter) {
		final String sqlFilter;
		if (filter == null) {
			sqlFilter = null;
		} else {
			final String eqFilter = filter.replaceAll("eq", "=");
			final String neFilter = eqFilter.replaceAll("ne", "!=");
			final String gtFilter = neFilter.replaceAll("gt", ">");
			final String ltFilter = gtFilter.replaceAll("lt", "<");
			final String slashFilter = ltFilter.replaceAll("\\\\", "\\\\\\\\");
			sqlFilter = slashFilter.replaceAll("'", "''"); // TODO flush this out a bit more
		}
		return workoutMapper.list(userId, start, max, sqlFilter);
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

	public List<WorkoutReport> createReport(final Long userId) {
		final Map<String, WorkoutReport> workoutMap = new TreeMap<>();
		final Calendar workoutDate = Calendar.getInstance();
		for (final Workout workout : workoutMapper.list(userId, null, null, null)) {
			workoutDate.setTimeInMillis(workout.getDateMs());
			final Integer year = workoutDate.get(Calendar.YEAR);
			final Integer week = workoutDate.get(Calendar.WEEK_OF_YEAR);
			final String workoutKey = String.format("%d-%d", year, week);
			final WorkoutReport workoutReport;
			if (workoutMap.containsKey(workoutKey)) {
				workoutReport = workoutMap.get(workoutKey);
			} else {
				workoutReport = new WorkoutReport() {
					{
						setUserId(userId);
						setYear(year);
						setWeek(week);
						setWorkouts(0);
						setTotalDistance(0.0);
						setTotalDuration(0.0);
					}
				};
				workoutMap.put(workoutKey, workoutReport);
			}
			workoutReport.setWorkouts(workoutReport.getWorkouts() + 1);
			workoutReport.setTotalDistance(workoutReport.getTotalDistance() + workout.getDistance());
			workoutReport.setTotalDuration(workoutReport.getTotalDuration() + workout.getDuration());
		}
		final List<WorkoutReport> fullWorkoutReport = new ArrayList<>();
		for (final String workoutKey : workoutMap.keySet()) {
			final WorkoutReport workoutReport = workoutMap.get(workoutKey);
			workoutReport.setAverageDistance(workoutReport.getTotalDistance() / workoutReport.getWorkouts());
			workoutReport.setAverageDuration(workoutReport.getTotalDuration() / workoutReport.getWorkouts());
			fullWorkoutReport.add(workoutReport);
		}
		return fullWorkoutReport;
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
