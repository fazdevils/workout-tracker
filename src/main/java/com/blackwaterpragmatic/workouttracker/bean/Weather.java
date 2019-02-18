package com.blackwaterpragmatic.workouttracker.bean;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class Weather {
	private Long dateMs;
	private String postalCode;
	private String weather;

	public Long getDateMs() {
		return dateMs;
	}

	public void setDateMs(final Long dateMs) {
		this.dateMs = dateMs;
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
