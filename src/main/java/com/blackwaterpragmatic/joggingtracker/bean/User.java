package com.blackwaterpragmatic.joggingtracker.bean;


import com.blackwaterpragmatic.joggingtracker.constant.Role;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import java.util.Set;

import io.swagger.annotations.ApiModel;

@ApiModel
@JsonIgnoreProperties(value = {
		"bitwiseRole"})
@JsonInclude(Include.NON_NULL)
public class User {

	private Long id;
	private String login;
	private Set<Role> roles;
	private Integer bitwiseRole;
	private Boolean active;

	public Long getId() {
		return id;
	}

	public void setId(final Long id) {
		this.id = id;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(final String login) {
		this.login = login;
	}

	public Set<Role> getRoles() {
		return roles;
	}

	public void setRoles(final Set<Role> roles) {
		this.roles = roles;
	}

	public Integer getBitwiseRole() {
		return bitwiseRole;
	}

	public void setBitwiseRole(final Integer bitwiseRole) {
		this.bitwiseRole = bitwiseRole;
	}

	public Boolean getActive() {
		return active;
	}

	public void setActive(final Boolean active) {
		this.active = active;
	}

}
