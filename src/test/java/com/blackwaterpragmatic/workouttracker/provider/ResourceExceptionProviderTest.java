package com.blackwaterpragmatic.workouttracker.provider;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.blackwaterpragmatic.workouttracker.helper.DatabaseErrorHelper;
import com.blackwaterpragmatic.workouttracker.helper.ResponseHelper;
import com.blackwaterpragmatic.workouttracker.provider.ResourceExceptionProvider;
import com.blackwaterpragmatic.workouttracker.test.MockHelper;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.dao.DataAccessException;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

@RunWith(MockitoJUnitRunner.class)
public class ResourceExceptionProviderTest {

	@Mock
	private Throwable throwableException;

	@Mock
	private NotFoundException notFoundException;

	@Mock
	private DataAccessException dataAccessException;

	@Mock
	private DatabaseErrorHelper databaseErrorHelper;

	@Mock
	private ResponseHelper responseHelper;

	@InjectMocks
	private ResourceExceptionProvider resourceExceptionProvider;

	@Test
	public void should_handle_resteasy_not_found_exception() {
		final Status badRequestError = Response.Status.BAD_REQUEST;
		final Response expectedResponse = Response.status(badRequestError).build();

		when(notFoundException.getMessage()).thenReturn(badRequestError.getReasonPhrase());
		when(responseHelper.build(badRequestError, badRequestError.getReasonPhrase()))
				.thenReturn(expectedResponse);

		final Response response = resourceExceptionProvider.toResponse(notFoundException);

		verify(notFoundException, times(3)).getMessage();
		verify(notFoundException).getLocalizedMessage();
		verify(notFoundException).getStackTrace();
		verify(notFoundException).getCause();
		verify(responseHelper).build(badRequestError, badRequestError.getReasonPhrase());
		verifyNoMoreInteractions(MockHelper.allDeclaredMocks(this));

		assertEquals(expectedResponse, response);
	}

	@Test
	public void should_handle_resteasy_not_found_exception_with_cause() {
		final String causeDescription = "test cause description";
		final Throwable cause = new Throwable(causeDescription);

		final Status badRequestError = Response.Status.BAD_REQUEST;
		final Response expectedResponse = Response.status(badRequestError).build();

		when(notFoundException.getCause()).thenReturn(cause);
		when(responseHelper.build(badRequestError, causeDescription))
				.thenReturn(expectedResponse);

		final Response response = resourceExceptionProvider.toResponse(notFoundException);

		verify(notFoundException, times(2)).getMessage();
		verify(notFoundException).getLocalizedMessage();
		verify(notFoundException).getStackTrace();
		verify(notFoundException, times(2)).getCause();
		verify(responseHelper).build(badRequestError, causeDescription);
		verifyNoMoreInteractions(MockHelper.allDeclaredMocks(this));

		assertEquals(expectedResponse, response);
	}

	@Test
	public void should_handle_data_access_exception() {
		final String validationError = "validationError";
		final Status badRequestError = Response.Status.BAD_REQUEST;
		final Response expectedResponse = Response.status(badRequestError).build();

		when(databaseErrorHelper.getDatabaseError(dataAccessException)).thenReturn(validationError);
		when(responseHelper.build(badRequestError, validationError))
				.thenReturn(expectedResponse);

		final Response response = resourceExceptionProvider.toResponse(dataAccessException);

		verify(dataAccessException, times(2)).getMessage();
		verify(dataAccessException).getLocalizedMessage();
		verify(dataAccessException).getStackTrace();
		verify(databaseErrorHelper).getDatabaseError(dataAccessException);
		verify(responseHelper).build(badRequestError, validationError);
		verifyNoMoreInteractions(MockHelper.allDeclaredMocks(this));

		assertEquals(expectedResponse, response);
	}

	@Test
	public void should_handle_other_exception() {
		final Response expectedResponse = Response.serverError().build();
		final Status internalServerError = Response.Status.INTERNAL_SERVER_ERROR;

		when(responseHelper.build(internalServerError, internalServerError.getReasonPhrase()))
				.thenReturn(expectedResponse);

		final Response response = resourceExceptionProvider.toResponse(throwableException);

		verify(throwableException, times(2)).getMessage();
		verify(throwableException).getLocalizedMessage();
		verify(throwableException).getStackTrace();
		verify(responseHelper).build(internalServerError, internalServerError.getReasonPhrase());
		verifyNoMoreInteractions(MockHelper.allDeclaredMocks(this));

		assertEquals(expectedResponse, response);
	}

}
