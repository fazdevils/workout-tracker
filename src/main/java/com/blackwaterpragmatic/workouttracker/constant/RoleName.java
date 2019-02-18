package com.blackwaterpragmatic.workouttracker.constant;

/**
 * Corresponds to Role enum. Required because annotations won't allow use of Role.{value}.toString()
 */
public interface RoleName {
	String USER = "USER";
	String USER_MANAGER = "USER_MANAGER";
	String ADMIN = "ADMIN";
}
