package com.blackwaterpragmatic.joggingtracker.bean;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.annotations.ApiModel;

@ApiModel
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuthenticationToken {

	private final String token;

	public AuthenticationToken(final String token) {
		this.token = token;
	}

	public String getToken() {
		return token;
	}

}
