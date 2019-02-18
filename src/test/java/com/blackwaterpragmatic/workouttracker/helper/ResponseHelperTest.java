package com.blackwaterpragmatic.workouttracker.helper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import com.blackwaterpragmatic.workouttracker.bean.ResponseMessage;
import com.blackwaterpragmatic.workouttracker.constant.MediaType;
import com.blackwaterpragmatic.workouttracker.helper.ResponseHelper;

import org.junit.Test;

import javax.ws.rs.core.Response;

import java.net.URI;
import java.net.URISyntaxException;

public class ResponseHelperTest {

	private final ResponseHelper responseHelper = new ResponseHelper();

	@Test
	public void should_build_entity_response() {
		final Object testObject = new Object();
		final Response response = responseHelper.build(Response.Status.OK, testObject);
		assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
		assertNull(response.getLocation());
		assertEquals(testObject, response.getEntity());
		assertEquals(MediaType.JSON, response.getMediaType().toString());
	}

	@Test
	public void should_build_string_response() {
		final Object testObject = "plain text message";
		final Response response = responseHelper.build(Response.Status.OK, testObject);
		assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
		assertNull(response.getLocation());
		assertEquals(testObject, ((ResponseMessage) response.getEntity()).getMessage());
		assertEquals(MediaType.JSON, response.getMediaType().toString());
	}

	@Test
	public void should_build_entity_location_response() throws URISyntaxException {
		final URI testLocation = new URI("/test/uri");
		final Object testObject = new Object();
		final Response response = responseHelper.build(Response.Status.CREATED, testLocation, testObject);
		assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
		assertEquals(testLocation, response.getLocation());
		assertEquals(testObject, response.getEntity());
		assertEquals(MediaType.JSON, response.getMediaType().toString());
	}

	@Test
	public void should_build_null_entity_response() {
		final Response response = responseHelper.build(Response.Status.OK, null);
		assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
		assertNull(response.getLocation());
		assertNull(response.getEntity());
		assertEquals(MediaType.JSON, response.getMediaType().toString());
	}

}
