package com.blackwaterpragmatic.workouttracker.bean.internal;

public class ApplicationEnvironment {

	private String jwtSignature;
	private Integer jwtExpirationHours;
	private Integer jwtExpirationMinutes;
	private String apiVersion;
	private String apiSchemes;
	private String apiHost;
	private String apiTitle;
	private String buildTime;
	private String weatherUrl;
	private String env;

	public String getJwtSignature() {
		return jwtSignature;
	}

	public void setJwtSignature(final String jwtSignature) {
		this.jwtSignature = jwtSignature;
	}

	public Integer getJwtExpirationHours() {
		return jwtExpirationHours;
	}

	public void setJwtExpirationHours(final Integer jwtExpirationHours) {
		this.jwtExpirationHours = jwtExpirationHours;
	}

	public Integer getJwtExpirationMinutes() {
		return jwtExpirationMinutes;
	}

	public void setJwtExpirationMinutes(final Integer jwtExpirationMinutes) {
		this.jwtExpirationMinutes = jwtExpirationMinutes;
	}

	public String getApiVersion() {
		return apiVersion;
	}

	public void setApiVersion(final String apiVersion) {
		this.apiVersion = apiVersion;
	}

	public String getApiSchemes() {
		return apiSchemes;
	}

	public void setApiSchemes(final String apiSchemes) {
		this.apiSchemes = apiSchemes;
	}

	public String getApiHost() {
		return apiHost;
	}

	public void setApiHost(final String apiHost) {
		this.apiHost = apiHost;
	}

	public String getApiTitle() {
		return apiTitle;
	}

	public void setApiTitle(final String apiTitle) {
		this.apiTitle = apiTitle;
	}

	public String getBuildTime() {
		return buildTime;
	}

	public void setBuildTime(final String buildTime) {
		this.buildTime = buildTime;
	}

	public String getWeatherUrl() {
		return weatherUrl;
	}

	public void setWeatherUrl(final String weatherUrl) {
		this.weatherUrl = weatherUrl;
	}

	public String getEnv() {
		return env;
	}

	public void setEnv(final String env) {
		this.env = env;
	}

}
