package com.blackwaterpragmatic.workouttracker.resource;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.blackwaterpragmatic.workouttracker.bean.ApplicationStatus;
import com.blackwaterpragmatic.workouttracker.bean.internal.ApplicationEnvironment;
import com.blackwaterpragmatic.workouttracker.helper.ResponseHelper;
import com.blackwaterpragmatic.workouttracker.resource.ApplicationStatusResource;
import com.blackwaterpragmatic.workouttracker.test.MockHelper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.mock.MockHttpRequest;
import org.jboss.resteasy.mock.MockHttpResponse;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;

@RunWith(MockitoJUnitRunner.class)
public class ApplicationStatusResourceTest {

	@Mock
	private ApplicationEnvironment applicationEnvironment;

	@Mock
	private HttpServletRequest httpServletRequest;

	private ApplicationStatusResource applicationStatusResource;

	@Before
	public void before() {
		// using ResponseHelper directly instead of mocking to verify actual response
		applicationStatusResource = new ApplicationStatusResource(new ResponseHelper(), applicationEnvironment);

		ResteasyProviderFactory.getContextDataMap().put(HttpServletRequest.class, httpServletRequest);
	}

	@Test
	public void should_report_release_status() throws JsonProcessingException, URISyntaxException, UnsupportedEncodingException {
		final ApplicationStatus applicationStatus = new ApplicationStatus() {
			{
				setApiVersion("api version");
				setBuildTime("build time");
				setEnv("env");
			}
		};

		when(applicationEnvironment.getApiVersion()).thenReturn(applicationStatus.getApiVersion());
		when(applicationEnvironment.getBuildTime()).thenReturn(applicationStatus.getBuildTime());
		when(applicationEnvironment.getEnv()).thenReturn(applicationStatus.getEnv());

		final Dispatcher dispatcher = MockHelper.createMockDispatcher(applicationStatusResource);

		final String expectedResponse = new ObjectMapper().writeValueAsString(applicationStatus);

		final MockHttpRequest request = MockHttpRequest.get("/");
		final MockHttpResponse response = new MockHttpResponse();

		dispatcher.invoke(request, response);

		verify(applicationEnvironment).getApiVersion();
		verify(applicationEnvironment).getBuildTime();
		verify(applicationEnvironment).getEnv();

		assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
		assertEquals(expectedResponse, response.getContentAsString());
	}

}
