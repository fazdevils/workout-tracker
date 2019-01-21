package com.blackwaterpragmatic.joggingtracker.helper;

import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;

@Component
public class DatabaseErrorHelper {

	public String getDatabaseError(final DataAccessException databaseException) {
		Throwable cause = databaseException;
		while (null != cause.getCause()) {
			cause = databaseException.getCause();
		}
		return cause.getMessage();
	}
}
