package com.blackwaterpragmatic.workouttracker.helper;

import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;

@Component
public class DatabaseErrorHelper {

	public String getDatabaseError(final DataAccessException databaseException) {
		Throwable cause = databaseException;
		while (null != cause.getCause()) {
			cause = databaseException.getMostSpecificCause();
		}
		return cause.getMessage();
	}
}
