package com.blackwaterpragmatic.joggingtracker.resource;

import static com.blackwaterpragmatic.joggingtracker.constant.RequestScopeAttribute.SWAGGER_READER_CONFIG;
import static com.blackwaterpragmatic.joggingtracker.test.MockHelper.createMockDispatcher;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.blackwaterpragmatic.joggingtracker.bean.internal.ApplicationEnvironment;
import com.blackwaterpragmatic.joggingtracker.helper.ResponseHelper;
import com.blackwaterpragmatic.joggingtracker.test.MockHelper;

import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.mock.MockHttpRequest;
import org.jboss.resteasy.mock.MockHttpResponse;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;

import java.net.URISyntaxException;

import io.swagger.jaxrs.config.BeanConfig;

@RunWith(MockitoJUnitRunner.class)
public class ApiListingResourceTest {

	@Mock
	private HttpServletRequest httpServletRequest;

	@Mock
	private ServletContext servletContext;

	@Mock
	private BeanConfig swaggerApiConfig;

	@Mock
	private ApplicationEnvironment applicationEnvironment;

	private ApiListingResource apiListingResource;

	@Before
	public void before() {
		ResteasyProviderFactory.getContextDataMap().put(HttpServletRequest.class, httpServletRequest);

		apiListingResource = new ApiListingResource(swaggerApiConfig, new ResponseHelper(), applicationEnvironment);
	}

	@Test
	public void should_get_json_listing() throws URISyntaxException {
		final Dispatcher dispatcher = createMockDispatcher(apiListingResource);

		when(httpServletRequest.getServletContext()).thenReturn(servletContext);

		final MockHttpRequest request = MockHttpRequest.get("/api/swagger.json");
		final MockHttpResponse response = new MockHttpResponse();

		dispatcher.invoke(request, response);

		verify(httpServletRequest).getServletContext();
		verify(applicationEnvironment).getEnv();
		verify(servletContext, atLeast(0)).getAttribute(anyString()); // this is verifying internal Swagger calls
		verify(servletContext).setAttribute(SWAGGER_READER_CONFIG, swaggerApiConfig);
		verifyNoMoreInteractions(MockHelper.allDeclaredMocks(this));
		reset(MockHelper.allDeclaredMocks(this));
	}

	@Test
	public void should_not_get_production_json_listing() throws URISyntaxException {
		reset(MockHelper.allDeclaredMocks(this));

		when(applicationEnvironment.getEnv()).thenReturn("prod");

		apiListingResource = new ApiListingResource(swaggerApiConfig, new ResponseHelper(), applicationEnvironment);
		final Dispatcher dispatcher = createMockDispatcher(apiListingResource);

		final MockHttpRequest request = MockHttpRequest.get("/api/swagger.json");
		final MockHttpResponse response = new MockHttpResponse();

		dispatcher.invoke(request, response);

		verify(applicationEnvironment).getEnv();
		verifyNoMoreInteractions(MockHelper.allDeclaredMocks(this));
		reset(MockHelper.allDeclaredMocks(this));

		assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
	}

}
