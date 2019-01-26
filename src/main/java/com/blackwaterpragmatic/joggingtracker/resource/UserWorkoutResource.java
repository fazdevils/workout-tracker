package com.blackwaterpragmatic.joggingtracker.resource;

import static com.blackwaterpragmatic.joggingtracker.constant.MediaType.JSON;
import static com.blackwaterpragmatic.joggingtracker.constant.RequestScopeAttribute.AUTHENTICATED_USER;
import static com.blackwaterpragmatic.joggingtracker.constant.RoleName.USER;

import com.blackwaterpragmatic.joggingtracker.bean.User;
import com.blackwaterpragmatic.joggingtracker.bean.Workout;
import com.blackwaterpragmatic.joggingtracker.helper.ResponseHelper;
import com.blackwaterpragmatic.joggingtracker.service.WorkoutService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Authorization;

@Service
@Path("/user/workout")
@Api(value = "User Settings",
		authorizations = {
				@Authorization(
						value = "JWT")
		})
public class UserWorkoutResource {

	private static final String WORKOUT_ID = "workoutId";

	private final WorkoutService workoutService;
	private final ResponseHelper responseHelper;

	@Autowired
	public UserWorkoutResource(
			final WorkoutService workoutService,
			final ResponseHelper responseHelper) {
		this.workoutService = workoutService;
		this.responseHelper = responseHelper;
	}

	@RolesAllowed(USER)
	@POST
	@Consumes(JSON)
	@Produces(JSON)
	@ApiOperation(value = "Add a workout")
	@ApiResponses({
			@ApiResponse(
					code = HttpServletResponse.SC_CREATED,
					message = "Success"),
			@ApiResponse(
					code = HttpServletResponse.SC_BAD_REQUEST,
					message = "Bad request. Cause(s) returned in the response.")
	})
	public Response addWorkout(
			@ApiParam(required = true) final Workout workout,
			@ApiParam(hidden = true) @Context final HttpServletRequest request) throws URISyntaxException, IOException {
		final User authenticatedUser = (User) request.getAttribute(AUTHENTICATED_USER);
		workout.setUserId(authenticatedUser.getId());
		final Workout newWorkout = workoutService.addWorkout(workout);

		return responseHelper.build(Response.Status.CREATED, new URI("/user/workout/" + newWorkout.getId()), newWorkout);
	}

	@RolesAllowed(USER)
	@GET
	@Produces(JSON)
	@ApiOperation(value = "Get all workouts")
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
	public Response getAllWorkouta(
			@ApiParam(hidden = true) @Context final HttpServletRequest request) {
		final User authenticatedUser = (User) request.getAttribute(AUTHENTICATED_USER);
		final List<Workout> workouts = workoutService.getWorkouts(authenticatedUser.getId());

		return responseHelper.build(Response.Status.OK, workouts);
	}

	@RolesAllowed(USER)
	@Path("/{" + WORKOUT_ID + "}")
	@GET
	@Produces(JSON)
	@ApiOperation(value = "Get workout")
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
	public Response getWorkout(
			@PathParam(WORKOUT_ID) final Long workoutId,
			@ApiParam(hidden = true) @Context final HttpServletRequest request) {
		final User authenticatedUser = (User) request.getAttribute(AUTHENTICATED_USER);
		final Workout workout = workoutService.getWorkout(authenticatedUser.getId(), workoutId);

		if (null == workout) {
			return responseHelper.build(Response.Status.NOT_FOUND, "Workout not found.");
		} else {
			return responseHelper.build(Response.Status.OK, workout);
		}
	}

	@RolesAllowed(USER)
	@Path("/{" + WORKOUT_ID + "}")
	@PUT
	@Consumes(JSON)
	@Produces(JSON)
	@ApiOperation(value = "Update workout")
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
			@PathParam(WORKOUT_ID) final Long workoutId,
			@ApiParam(required = true) final Workout workout,
			@ApiParam(hidden = true) @Context final HttpServletRequest request) {
		final User authenticatedUser = (User) request.getAttribute(AUTHENTICATED_USER);
		workout.setUserId(authenticatedUser.getId());
		workout.setId(workoutId);
		final Workout updatedWorkout = workoutService.updateWorkout(workout);

		if (null == updatedWorkout) {
			return responseHelper.build(Response.Status.NOT_FOUND, "Workout not found.");
		} else {
			return responseHelper.build(Response.Status.OK, updatedWorkout);
		}
	}

	@RolesAllowed(USER)
	@Path("/{" + WORKOUT_ID + "}")
	@DELETE
	@Produces(JSON)
	@ApiOperation(value = "Delete workout")
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
	public Response deleteWorkout(
			@PathParam(WORKOUT_ID) final Long workoutId,
			@ApiParam(hidden = true) @Context final HttpServletRequest request) {
		final User authenticatedUser = (User) request.getAttribute(AUTHENTICATED_USER);
		workoutService.deleteWorkout(authenticatedUser.getId(), workoutId);

		return responseHelper.build(Response.Status.NO_CONTENT, null);
	}

}
