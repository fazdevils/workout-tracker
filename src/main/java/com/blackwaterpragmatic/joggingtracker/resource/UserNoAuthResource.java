package com.blackwaterpragmatic.joggingtracker.resource;

import static com.blackwaterpragmatic.joggingtracker.constant.MediaType.JSON;

import com.blackwaterpragmatic.joggingtracker.bean.AuthenticationToken;
import com.blackwaterpragmatic.joggingtracker.bean.Credentials;
import com.blackwaterpragmatic.joggingtracker.bean.NewUser;
import com.blackwaterpragmatic.joggingtracker.bean.User;
import com.blackwaterpragmatic.joggingtracker.helper.ResponseHelper;
import com.blackwaterpragmatic.joggingtracker.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.security.PermitAll;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;

import java.net.URI;
import java.net.URISyntaxException;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@Service
@Path("/user")
@Api(value = "User Settings")
public class UserNoAuthResource {

	private final UserService userService;
	private final ResponseHelper responseHelper;

	@Autowired
	public UserNoAuthResource(
			final UserService userService,
			final ResponseHelper responseHelper) {
		this.userService = userService;
		this.responseHelper = responseHelper;
	}

	@PermitAll
	@Path("/register")
	@POST
	@Consumes(JSON)
	@Produces(JSON)
	@ApiOperation(value = "Register as a new user")
	@ApiResponses({
			@ApiResponse(
					code = HttpServletResponse.SC_CREATED,
					message = "Success"),
			@ApiResponse(
					code = HttpServletResponse.SC_BAD_REQUEST,
					message = "Bad request. Cause(s) returned in the response.")
	})
	public Response registerUser(@ApiParam(required = true) final NewUser user) throws URISyntaxException {
		final User newUser = userService.registerUser(user, false);
		return responseHelper.build(Response.Status.CREATED, new URI("/user"), newUser);
	}

	@PermitAll
	@Path("/authenticate")
	@POST
	@Consumes(JSON)
	@Produces(JSON)
	@ApiResponses({
			@ApiResponse(
					code = HttpServletResponse.SC_CREATED,
					message = "Success"),
			@ApiResponse(
					code = HttpServletResponse.SC_BAD_REQUEST,
					message = "Bad request. Cause(s) returned in the response.")
	})
	public Response authenticate(
			@ApiParam(required = true) final Credentials credentials,
			@ApiParam(hidden = true) @HeaderParam(HttpHeaders.USER_AGENT) final String userAgent,
			@ApiParam(hidden = true) @Context final HttpServletRequest request) {

		final String authenticationToken = userService.buildAuthenticationToken(
				credentials,
				userAgent,
				request);

		if (null == authenticationToken) {
			return responseHelper.build(Response.Status.UNAUTHORIZED, "Invalid credentials.");
		} else {
			return responseHelper.build(Response.Status.CREATED, new AuthenticationToken(authenticationToken));
		}
	}

}
