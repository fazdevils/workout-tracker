package com.blackwaterpragmatic.joggingtracker.resource;

import static com.blackwaterpragmatic.joggingtracker.constant.MediaType.JSON;
import static com.blackwaterpragmatic.joggingtracker.constant.RoleName.USER_MANAGER;

import com.blackwaterpragmatic.joggingtracker.bean.Password;
import com.blackwaterpragmatic.joggingtracker.bean.User;
import com.blackwaterpragmatic.joggingtracker.helper.ResponseHelper;
import com.blackwaterpragmatic.joggingtracker.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import java.util.List;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Authorization;

@Service
@Path("/user-management/users")
@Api(value = "User Management",
		authorizations = {
				@Authorization(
						value = "JWT")
		})
public class UserManagementResource {

	private static final String MAX = "max";
	private static final String START = "start";
	private static final String USER_ID = "userId";

	private final UserService userService;
	private final ResponseHelper responseHelper;

	@Autowired
	public UserManagementResource(
			final UserService userService,
			final ResponseHelper responseHelper) {
		this.userService = userService;
		this.responseHelper = responseHelper;
	}

	@RolesAllowed(USER_MANAGER)
	@GET
	@Produces(JSON)
	@ApiOperation(value = "Get users")
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
	public Response getUsers(
			@ApiParam(value = "The first result to return") @QueryParam(START) final Integer start,
			@ApiParam(value = "The maximum number of results to return") @QueryParam(MAX) final Integer max) {
		final List<User> users = userService.listUsers(start, max);

		return responseHelper.build(Response.Status.OK, users);
	}

	@RolesAllowed(USER_MANAGER)
	@Path("/{" + USER_ID + "}")
	@GET
	@Produces(JSON)
	@ApiOperation(value = "Get a user")
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
	public Response getUser(@PathParam(USER_ID) final Long userId) {
		final User user = userService.getUser(userId);

		if (null == user) {
			return responseHelper.build(Response.Status.NOT_FOUND, "User not found.");
		} else {
			return responseHelper.build(Response.Status.OK, user);
		}
	}

	@RolesAllowed(USER_MANAGER)
	@Path("/{" + USER_ID + "}")
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
			@PathParam(USER_ID) final Long userId,
			@ApiParam(required = true) final User user) {
		user.setId(userId);
		final User updatedUser = userService.updateUser(user, true);

		if (null == updatedUser) {
			return responseHelper.build(Response.Status.NOT_FOUND, "User not found.");
		} else {
			return responseHelper.build(Response.Status.OK, updatedUser);
		}
	}

	@RolesAllowed(USER_MANAGER)
	@Path("/{" + USER_ID + "}/deactivate")
	@POST
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
	public Response deactivateUser(
			@PathParam(USER_ID) final Long userId) {
		userService.deactivate(userId);

		return responseHelper.build(Response.Status.NO_CONTENT, null);
	}

	@RolesAllowed(USER_MANAGER)
	@Path("/{" + USER_ID + "}/activate")
	@POST
	@Consumes(JSON)
	@Produces(JSON)
	@ApiOperation(value = "Activate user")
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
	public Response activateUser(
			@PathParam(USER_ID) final Long userId) {
		userService.activate(userId);

		return responseHelper.build(Response.Status.NO_CONTENT, null);
	}

	@RolesAllowed(USER_MANAGER)
	@Path("/{" + USER_ID + "}/password")
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
			@PathParam(USER_ID) final Long userId,
			@ApiParam(required = true) final Password password) {
		userService.updatePassword(userId, password.getPassword());

		return responseHelper.build(Response.Status.NO_CONTENT, null);
	}

}
