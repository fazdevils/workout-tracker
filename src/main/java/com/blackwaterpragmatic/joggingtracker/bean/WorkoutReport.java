package com.blackwaterpragmatic.joggingtracker.bean;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import io.swagger.annotations.ApiModel;

@ApiModel
@JsonInclude(Include.NON_NULL)
public class WorkoutReport {

	private Long userId;
	private Integer year;
	private Integer week;
	private Integer workouts;
	private Double totalDistance;
	private Double totalDuration;
	private Double averageDistance;
	private Double averageDuration;

	public Long getUserId() {
		return userId;
	}

	public void setUserId(final Long userId) {
		this.userId = userId;
	}

	public Integer getYear() {
		return year;
	}

	public void setYear(final Integer year) {
		this.year = year;
	}

	public Integer getWorkouts() {
		return workouts;
	}

	public void setWorkouts(final Integer workouts) {
		this.workouts = workouts;
	}

	public Integer getWeek() {
		return week;
	}

	public void setWeek(final Integer week) {
		this.week = week;
	}

	public Double getTotalDistance() {
		return totalDistance;
	}

	public void setTotalDistance(final Double totalDistance) {
		this.totalDistance = totalDistance;
	}

	public Double getTotalDuration() {
		return totalDuration;
	}

	public void setTotalDuration(final Double totalDuration) {
		this.totalDuration = totalDuration;
	}

	public Double getAverageDistance() {
		return averageDistance;
	}

	public void setAverageDistance(final Double averageDistance) {
		this.averageDistance = averageDistance;
	}

	public Double getAverageDuration() {
		return averageDuration;
	}

	public void setAverageDuration(final Double averageDuration) {
		this.averageDuration = averageDuration;
	}

}
