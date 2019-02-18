package com.blackwaterpragmatic.workouttracker.bean;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import io.swagger.annotations.ApiModel;

@ApiModel
@JsonInclude(Include.NON_NULL)
public class Workout {

	private Long id;
	private Long userId;
	private Long dateMs;
	private Double distance;
	private Double duration;
	private String postalCode;
	private String weather;

	public Long getId() {
		return id;
	}

	public void setId(final Long id) {
		this.id = id;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(final Long userId) {
		this.userId = userId;
	}

	public Long getDateMs() {
		return dateMs;
	}

	public void setDateMs(final Long dateMs) {
		this.dateMs = dateMs;
	}

	public Double getDistance() {
		return distance;
	}

	public void setDistance(final Double distance) {
		this.distance = distance;
	}

	public Double getDuration() {
		return duration;
	}

	public void setDuration(final Double duration) {
		this.duration = duration;
	}

	public String getPostalCode() {
		return postalCode;
	}

	public void setPostalCode(final String postalCode) {
		this.postalCode = postalCode;
	}

	public String getWeather() {
		return weather;
	}

	public void setWeather(final String weather) {
		this.weather = weather;
	}

}
