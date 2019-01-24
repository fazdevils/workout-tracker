package com.blackwaterpragmatic.joggingtracker.resource;

import static com.blackwaterpragmatic.joggingtracker.constant.MediaType.JSON;
import static com.blackwaterpragmatic.joggingtracker.constant.RequestScopeAttribute.AUTHENTICATED_USER;
import static com.blackwaterpragmatic.joggingtracker.constant.RoleName.USER;

import com.blackwaterpragmatic.joggingtracker.bean.Password;
import com.blackwaterpragmatic.joggingtracker.bean.User;
import com.blackwaterpragmatic.joggingtracker.helper.ResponseHelper;
import com.blackwaterpragmatic.joggingtracker.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Authorization;

@Service
@Path("/user")
@Api(value = "User Settings",
		authorizations = {
				@Authorization(
						value = "JWT")
		})
public class UserResource {

	private final UserService userService;
	private final ResponseHelper responseHelper;

	@Autowired
	public UserResource(
			final UserService userService,
			final ResponseHelper responseHelper) {
		this.userService = userService;
		this.responseHelper = responseHelper;
	}

	@RolesAllowed(USER)
	@GET
	@Produces(JSON)
	@ApiOperation(value = "Return user")
	@ApiResponses({
			@ApiResponse(
					code = HttpServletResponse.SC_OK,
					message = "Success"),
			@ApiResponse(
					code = HttpServletResponse.SC_BAD_REQUEST,
					message = "Bad request. Cause(s) returned in the response."),
			@ApiResponse(
					code = HttpServletResponse.SC_UNAUTHORIZED,
					message = "Invalid token. Reauthenticate.")
	})
	public Response getUser(@ApiParam(hidden = true) @Context final HttpServletRequest request) {
		final User authenticatedUser = (User) request.getAttribute(AUTHENTICATED_USER);
		final User user = userService.getUser(authenticatedUser.getId());

		if (null == user) {
			return responseHelper.build(Response.Status.UNAUTHORIZED, "Authenticated user not found.  Reauthenticate.");
		} else {
			return responseHelper.build(Response.Status.OK, user);
		}
	}

	@RolesAllowed(USER)
	@PUT
	@Consumes(JSON)
	@Produces(JSON)
	@ApiOperation(value = "Update user")
	@ApiResponses({
			@ApiResponse(
					code = HttpServletResponse.SC_OK,
					message = "Success"),
			@ApiResponse(
					code = HttpServletResponse.SC_BAD_REQUEST,
					message = "Bad request. Cause(s) returned in the response."),
			@ApiResponse(
					code = HttpServletResponse.SC_UNAUTHORIZED,
					message = "Invalid token. Reauthenticate.")
	})
	public Response updateUser(
			@ApiParam(required = true) final User user,
			@ApiParam(hidden = true) @Context final HttpServletRequest request) {
		final User authenticatedUser = (User) request.getAttribute(AUTHENTICATED_USER);
		final User updatedUser = userService.updateUser(authenticatedUser.getId(), user, false);

		return responseHelper.build(Response.Status.OK, updatedUser);
	}

	@RolesAllowed(USER)
	@DELETE
	@Consumes(JSON)
	@Produces(JSON)
	@ApiOperation(value = "Deactivate user")
	@ApiResponses({
			@ApiResponse(
					code = HttpServletResponse.SC_NO_CONTENT,
					message = "Success"),
			@ApiResponse(
					code = HttpServletResponse.SC_BAD_REQUEST,
					message = "Bad request. Cause(s) returned in the response."),
			@ApiResponse(
					code = HttpServletResponse.SC_UNAUTHORIZED,
					message = "Invalid token. Reauthenticate.")
	})
	public Response deactivateUser(@ApiParam(hidden = true) @Context final HttpServletRequest request) {
		final User authenticatedUser = (User) request.getAttribute(AUTHENTICATED_USER);
		userService.deactivate(authenticatedUser.getId());
		return responseHelper.build(Response.Status.NO_CONTENT, null);
	}

	@RolesAllowed(USER)
	@Path("/password")
	@PUT
	@Consumes(JSON)
	@Produces(JSON)
	@ApiOperation(value = "Update password")
	@ApiResponses({
			@ApiResponse(
					code = HttpServletResponse.SC_NO_CONTENT,
					message = "Success"),
			@ApiResponse(
					code = HttpServletResponse.SC_BAD_REQUEST,
					message = "Bad request. Cause(s) returned in the response."),
			@ApiResponse(
					code = HttpServletResponse.SC_UNAUTHORIZED,
					message = "Invalid token. Reauthenticate.")
	})
	public Response updatePassword(
			@ApiParam(required = true) final Password password,
			@ApiParam(hidden = true) @Context final HttpServletRequest request) {
		final User authenticatedUser = (User) request.getAttribute(AUTHENTICATED_USER);
		userService.updatePassword(authenticatedUser.getId(), password.getPassword());

		return responseHelper.build(Response.Status.NO_CONTENT, null);
	}

}
