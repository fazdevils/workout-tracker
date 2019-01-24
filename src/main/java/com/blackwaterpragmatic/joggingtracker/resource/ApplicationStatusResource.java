package com.blackwaterpragmatic.joggingtracker.resource;

import static com.blackwaterpragmatic.joggingtracker.constant.MediaType.JSON;

import com.blackwaterpragmatic.joggingtracker.bean.ApplicationStatus;
import com.blackwaterpragmatic.joggingtracker.bean.internal.ApplicationEnvironment;
import com.blackwaterpragmatic.joggingtracker.helper.ResponseHelper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.security.PermitAll;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import io.swagger.annotations.ApiOperation;

@Service
@Path("/")
public class ApplicationStatusResource {

	private final ApplicationEnvironment applicationEnvironment;
	private final ResponseHelper responseHelper;

	@Autowired
	public ApplicationStatusResource(
			final ResponseHelper responseHelper,
			final ApplicationEnvironment applicationEnvironment) {
		this.applicationEnvironment = applicationEnvironment;
		this.responseHelper = responseHelper;
	}

	@PermitAll
	@Path("/")
	@GET
	@Produces(JSON)
	@ApiOperation(value = "The application environment", hidden = true)
	public Response getApplicationStatus() {
		final ApplicationStatus applicationStatus = new ApplicationStatus() {
			{
				setApiVersion(applicationEnvironment.getApiVersion());
				setBuildTime(applicationEnvironment.getBuildTime());
				setEnv(applicationEnvironment.getEnv());
			}
		};

		return responseHelper.build(Response.Status.OK, applicationStatus);
	}

}

