package com.blackwaterpragmatic.joggingtracker.resource;

import static com.blackwaterpragmatic.joggingtracker.constant.MediaType.JSON;
import static com.blackwaterpragmatic.joggingtracker.constant.RequestScopeAttribute.SWAGGER_READER_CONFIG;

import com.blackwaterpragmatic.joggingtracker.bean.internal.ApplicationEnvironment;
import com.blackwaterpragmatic.joggingtracker.helper.ResponseHelper;
import com.fasterxml.jackson.core.JsonProcessingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.security.PermitAll;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import java.util.Enumeration;

import io.swagger.annotations.ApiOperation;
import io.swagger.jaxrs.config.BeanConfig;
import io.swagger.jaxrs.listing.BaseApiListingResource;

@Service
@Path("/api")
public class ApiListingResource extends BaseApiListingResource {

	private static final String PRODUCTION_ENVRONMENT = "prod";

	private final BeanConfig swaggerApiConfig;
	private final ResponseHelper responseHelper;
	private final Boolean isProduction;

	@Autowired
	public ApiListingResource(
			final BeanConfig swaggerApiConfig,
			final ResponseHelper responseHelper,
			final ApplicationEnvironment applicationEnvironment) {
		this.swaggerApiConfig = swaggerApiConfig;
		this.responseHelper = responseHelper;
		isProduction = PRODUCTION_ENVRONMENT.equalsIgnoreCase(applicationEnvironment.getEnv());
	}

	@PermitAll
	@Path("/swagger.json")
	@GET
	@Produces(JSON)
	@ApiOperation(value = "The swagger definition in JSON", hidden = true)
	public Response getJsonListing(
			@Context final Application app,
			@Context final HttpHeaders headers,
			@Context final UriInfo uriInfo,
			@Context final HttpServletRequest request) throws JsonProcessingException {

		if (isProduction) {
			return responseHelper.build(Response.Status.NOT_FOUND, null, JSON);
		}

		final ServletContext servletContext = getServletContext(request);
		final SwaggerServletConfig servletConfig = getServletConfig(servletContext);

		return getListingJsonResponse(app, servletContext, servletConfig, headers, uriInfo);
	}

	private ServletContext getServletContext(final HttpServletRequest request) {
		final ServletContext servletContext = request.getServletContext();
		servletContext.setAttribute(SWAGGER_READER_CONFIG, swaggerApiConfig);
		return servletContext;
	}

	private SwaggerServletConfig getServletConfig(final ServletContext servletContext) {
		return new SwaggerServletConfig(servletContext);
	}


	/***
	 * This is a bit of a hack, but needed, since Spring doesn't inject the ServletConfig
	 * through the @Context annotation. This has something to do with the way the project
	 * is configured through Spring. If we wanted to explore this further, we might be able
	 * to do something like what is described here:
	 * http://learningviacode.blogspot.com/2013/01/servletcontext-and-servletconfig.html
	 *
	 * This refers to the @RequestMapping annotation for SpringMVC which I'm not sure
	 * applies here.
	 */
	private static class SwaggerServletConfig implements ServletConfig {
		private final ServletContext servletContext;


		SwaggerServletConfig(final ServletContext servletContext) {
			this.servletContext = servletContext;
		}

		@Override
		public String getServletName() {
			return null;
		}

		@Override
		public ServletContext getServletContext() {
			return servletContext;
		}

		@Override
		public Enumeration<String> getInitParameterNames() {
			return null;
		}

		@Override
		public String getInitParameter(final String name) {
			return null;
		}
	}

}
