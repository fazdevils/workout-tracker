package com.blackwaterpragmatic.joggingtracker.resource;

import static com.blackwaterpragmatic.joggingtracker.constant.MediaType.JSON;
import static com.blackwaterpragmatic.joggingtracker.constant.RoleName.ADMIN;

import com.blackwaterpragmatic.joggingtracker.bean.Workout;
import com.blackwaterpragmatic.joggingtracker.helper.ResponseHelper;
import com.blackwaterpragmatic.joggingtracker.service.WorkoutService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
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
@Path("/admin/users")
@Api(value = "Admin",
		authorizations = {
				@Authorization(
						value = "JWT")
		})
public class AdminResource {

	private static final String USER_ID = "userId";
	private static final String WORKOUT_ID = "workoutId";

	private final WorkoutService workoutService;
	private final ResponseHelper responseHelper;

	@Autowired
	public AdminResource(
			final WorkoutService workoutService,
			final ResponseHelper responseHelper) {
		this.workoutService = workoutService;
		this.responseHelper = responseHelper;
	}

	@RolesAllowed(ADMIN)
	@Path("/{" + USER_ID + "}/workouts")
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
			@PathParam(USER_ID) final Long userId,
			@ApiParam(required = true) final Workout workout) throws URISyntaxException, IOException {
		workout.setUserId(userId);
		final Workout newWorkout = workoutService.addWorkout(workout);

		return responseHelper.build(Response.Status.CREATED, new URI("/user/workout/" + newWorkout.getId()), newWorkout);
	}

	@RolesAllowed(ADMIN)
	@Path("/{" + USER_ID + "}/workouts")
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
			@PathParam(USER_ID) final Long userId) {
		final List<Workout> workouts = workoutService.getWorkouts(userId);

		return responseHelper.build(Response.Status.OK, workouts);
	}

	@RolesAllowed(ADMIN)
	@Path("/{" + USER_ID + "}/workouts/{" + WORKOUT_ID + "}")
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
			@PathParam(USER_ID) final Long userId,
			@PathParam(WORKOUT_ID) final Long workoutId) {
		final Workout workout = workoutService.getWorkout(userId, workoutId);

		if (null == workout) {
			return responseHelper.build(Response.Status.NOT_FOUND, "Workout not found.");
		} else {
			return responseHelper.build(Response.Status.OK, workout);
		}
	}

	@RolesAllowed(ADMIN)
	@Path("/{" + USER_ID + "}/workouts/{" + WORKOUT_ID + "}")
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
			@PathParam(USER_ID) final Long userId,
			@PathParam(WORKOUT_ID) final Long workoutId,
			@ApiParam(required = true) final Workout workout) {
		workout.setUserId(userId);
		workout.setId(workoutId);
		final Workout updatedWorkout = workoutService.updateWorkout(workout);

		if (null == updatedWorkout) {
			return responseHelper.build(Response.Status.NOT_FOUND, "Workout not found.");
		} else {
			return responseHelper.build(Response.Status.OK, updatedWorkout);
		}
	}

	@RolesAllowed(ADMIN)
	@Path("/{" + USER_ID + "}/workouts/{" + WORKOUT_ID + "}")
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
			@PathParam(USER_ID) final Long userId,
			@PathParam(WORKOUT_ID) final Long workoutId) {
		workoutService.deleteWorkout(userId, workoutId);

		return responseHelper.build(Response.Status.NO_CONTENT, null);
	}

}
