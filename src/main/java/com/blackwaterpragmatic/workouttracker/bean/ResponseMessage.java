package com.blackwaterpragmatic.workouttracker.bean;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import io.swagger.annotations.ApiModel;

@ApiModel
@JsonInclude(Include.NON_NULL)
public class ResponseMessage {
	private final String message;

	public ResponseMessage(final String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

}
