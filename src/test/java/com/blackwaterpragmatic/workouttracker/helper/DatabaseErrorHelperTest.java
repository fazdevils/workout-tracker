package com.blackwaterpragmatic.workouttracker.helper;

import static org.junit.Assert.assertEquals;

import com.blackwaterpragmatic.workouttracker.helper.DatabaseErrorHelper;

import org.junit.Test;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;

public class DatabaseErrorHelperTest {

	private final DatabaseErrorHelper databaseErrorHelper = new DatabaseErrorHelper();

	@Test
	public void should_report_database_error() {
		final String expectedMessage = "some database constraint error";
		final DataAccessException databaseException = new DataIntegrityViolationException(expectedMessage);
		final String error = databaseErrorHelper.getDatabaseError(databaseException);
		assertEquals(expectedMessage, error);
	}

	@Test
	public void should_report_database_error_with_cause() {
		final String expectedMessage = "some database constraint error";
		final DataAccessException databaseException = new DataIntegrityViolationException("don't show", new Throwable(expectedMessage));
		final String error = databaseErrorHelper.getDatabaseError(databaseException);
		assertEquals(expectedMessage, error);
	}

}
