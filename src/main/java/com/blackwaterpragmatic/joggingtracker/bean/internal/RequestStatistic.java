package com.blackwaterpragmatic.joggingtracker.bean.internal;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "class")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RequestStatistic {

	private String method;
	private String url;
	private Long elapsedTime;
	private Long userId;
	private Integer responseCode;
	private String requestBody;

	public String getMethod() {
		return method;
	}

	public void setMethod(final String method) {
		this.method = method;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(final String url) {
		this.url = url;
	}

	public Long getElapsedTime() {
		return elapsedTime;
	}

	public void setElapsedTime(final Long elapsedTime) {
		this.elapsedTime = elapsedTime;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(final Long userId) {
		this.userId = userId;
	}

	public Integer getResponseCode() {
		return responseCode;
	}

	public void setResponseCode(final Integer responseCode) {
		this.responseCode = responseCode;
	}

	public String getRequestBody() {
		return requestBody;
	}

	public void setRequestBody(final String requestBody) {
		this.requestBody = requestBody;
	}

}
