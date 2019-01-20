package com.blackwaterpragmatic.joggingtracker.helper;

import com.blackwaterpragmatic.joggingtracker.bean.ValidationError;

import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;

@Component
public class DatabaseErrorHelper {

	public ValidationError reportDatabaseError(final DataAccessException databaseException) {
		final ValidationError validationError = new ValidationError();
		Throwable cause = databaseException.getCause();
		if (null == cause) {
			cause = databaseException;
		}
		validationError.setMessage(cause.getMessage());
		return validationError;
	}
}
