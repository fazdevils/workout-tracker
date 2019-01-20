package com.blackwaterpragmatic.joggingtracker.provider;

import static org.apache.logging.log4j.LogManager.getLogger;

import com.blackwaterpragmatic.joggingtracker.bean.ValidationError;
import com.blackwaterpragmatic.joggingtracker.helper.DatabaseErrorHelper;
import com.blackwaterpragmatic.joggingtracker.helper.ResponseHelper;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Component
@Provider
public class ResourceExceptionProvider implements ExceptionMapper<Throwable> {

	private static final Logger log = getLogger(ResourceExceptionProvider.class);

	private final ResponseHelper responseHelper;
	private final DatabaseErrorHelper databaseErrorHelper;

	@Autowired
	public ResourceExceptionProvider(
			final DatabaseErrorHelper databaseErrorHelper,
			final ResponseHelper responseHelper) {
		this.databaseErrorHelper = databaseErrorHelper;
		this.responseHelper = responseHelper;
	}

	@Override
	public Response toResponse(final Throwable exception) {

		/**
		 * Any error that's gotten this far is probably bad. Log the exception, and return to the caller
		 * with a generic message so that we're not exposing any implementation details.
		 */
		log.error(exception.getMessage(), exception);

		if (exception instanceof javax.ws.rs.NotFoundException) {
			final String message;
			if (null == exception.getCause()) {
				message = exception.getMessage();
			} else {
				message = exception.getCause().getMessage();
			}
			return responseHelper.build(Response.Status.BAD_REQUEST, message);
		} else if (exception instanceof org.springframework.dao.DataAccessException) {
			final ValidationError validationError = databaseErrorHelper.reportDatabaseError((DataAccessException) exception);
			return responseHelper.build(Response.Status.BAD_REQUEST, validationError);
		} else {
			final Status internalServerError = Response.Status.INTERNAL_SERVER_ERROR;
			return responseHelper.build(internalServerError, internalServerError.getReasonPhrase());
		}
	}

}
