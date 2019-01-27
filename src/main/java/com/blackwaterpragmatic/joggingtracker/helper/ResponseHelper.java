package com.blackwaterpragmatic.joggingtracker.helper;

import static com.blackwaterpragmatic.joggingtracker.constant.MediaType.JSON;

import com.blackwaterpragmatic.joggingtracker.bean.ResponseMessage;

import org.springframework.stereotype.Component;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import java.net.URI;

@Component
public class ResponseHelper {

	public Response build(
			final Response.Status status,
			final Object entity) {
		return buildResponse(status, null, entity);
	}

	public Response build(
			final Response.Status status,
			final URI location,
			final Object entity) {
		return buildResponse(status, location, entity);
	}

	private Response buildResponse(
			final Response.Status status,
			final URI location,
			final Object entity) {

		final ResponseBuilder responseBuilder = Response.status(status);

		if (null != location) {
			responseBuilder.location(location);
		}

		if (null != entity) {
			if (shouldWrapMessageText(entity)) {
				responseBuilder.entity(buildResponseMessage(entity.toString()));
			} else {
				responseBuilder.entity(entity);
			}
		}

		return responseBuilder.type(JSON).build();
	}

	private boolean shouldWrapMessageText(final Object entity) {
		return entity instanceof String;
	}

	private Object buildResponseMessage(final String entityString) {
		return new ResponseMessage(entityString);
	}

}
