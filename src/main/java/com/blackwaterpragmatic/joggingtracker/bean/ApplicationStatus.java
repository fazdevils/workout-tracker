package com.blackwaterpragmatic.joggingtracker.bean;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.annotations.ApiModel;

@ApiModel
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApplicationStatus {
	private String apiVersion;
	private String env;
	private String buildTime;

	public String getApiVersion() {
		return apiVersion;
	}

	public void setApiVersion(final String apiVersion) {
		this.apiVersion = apiVersion;
	}

	public String getEnv() {
		return env;
	}

	public void setEnv(final String env) {
		this.env = env;
	}

	public String getBuildTime() {
		return buildTime;
	}

	public void setBuildTime(final String buildTime) {
		this.buildTime = buildTime;
	}

}
