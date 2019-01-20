package com.blackwaterpragmatic.joggingtracker.resource;

import static com.blackwaterpragmatic.joggingtracker.constant.MediaType.JSON;
import static com.blackwaterpragmatic.joggingtracker.constant.RoleName.USER_MANAGER;

import com.blackwaterpragmatic.joggingtracker.bean.Error;
import com.blackwaterpragmatic.joggingtracker.bean.User;
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
@Path("/users")
@Api(value = "User Management",
		authorizations = {
				@Authorization(
						value = "JWT")
		})
public class UserManagementResource {

	private final UserService userService;

	@Autowired
	public UserManagementResource(
			final UserService userService) {
		this.userService = userService;
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

		return Response.status(Response.Status.OK).entity(users).build();
	}

	@RolesAllowed(USER_MANAGER)
	@Path("/{userId}")
	@GET
	@Produces(JSON)
	@ApiOperation(value = "Return a users")
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
			final Error error = new Error();
			error.setMessage("User not found.");
			return Response.status(Response.Status.NOT_FOUND).entity(error).build();
		} else {
			return Response.status(Response.Status.OK).entity(user).build();
		}
	}

}
