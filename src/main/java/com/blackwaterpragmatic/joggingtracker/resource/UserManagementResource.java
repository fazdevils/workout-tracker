package com.blackwaterpragmatic.joggingtracker.resource;

import static com.blackwaterpragmatic.joggingtracker.constant.MediaType.JSON;
import static com.blackwaterpragmatic.joggingtracker.constant.RoleName.USER_MANAGER;

import com.blackwaterpragmatic.joggingtracker.bean.User;
import com.blackwaterpragmatic.joggingtracker.helper.ResponseHelper;
import com.blackwaterpragmatic.joggingtracker.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import java.util.List;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
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
	@ApiOperation(value = "Return users")
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
	public Response getUsers() {
		final List<User> users = userService.listUsers();

		return responseHelper.build(Response.Status.OK, users);
	}

	@RolesAllowed(USER_MANAGER)
	@Path("/{userId}")
	@GET
	@Produces(JSON)
	@ApiOperation(value = "Return a user")
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
	public Response getUser(@PathParam("userId") final Long userId) {
		final User user = userService.getUser(userId);

		if (null == user) {
			return responseHelper.build(Response.Status.NOT_FOUND, "User not found.");
		} else {
			return responseHelper.build(Response.Status.OK, user);
		}
	}

}
