package com.blackwaterpragmatic.joggingtracker.mybatis.mapper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import com.blackwaterpragmatic.joggingtracker.bean.User;
import com.blackwaterpragmatic.joggingtracker.bean.Workout;
import com.blackwaterpragmatic.joggingtracker.spring.DataConfiguration;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
@ContextConfiguration(classes = {DataConfiguration.class})
@Rollback
@Transactional
@Component
public class WorkoutMapperTest {

	@Autowired
	private UserMapper userMapper;

	@Autowired
	private WorkoutMapper workoutMapper;

	@Test
	public void should_list_workouts() {
		final Long userId = getBasicUserId();
		final List<Workout> workouts = workoutMapper.list(userId);

		assertEquals(1, workouts.size());

		final Workout workout = workouts.get(0);
		assertNotNull(workout.getId());
		assertEquals(userId, workout.getUserId());
		assertEquals(Long.valueOf(1), workout.getDateMs());
		assertEquals(Double.valueOf(2.0), workout.getDistance());
		assertEquals(Double.valueOf(3.0), workout.getDuration());
		assertEquals("14004", workout.getPostalCode());
		assertEquals("SUNNY", workout.getWeather());
	}

	@Test
	public void should_fetch_workout() {
		final Long userId = getBasicUserId();
		final Long workoutId = workoutMapper.list(userId).get(0).getId();

		final Workout workout = workoutMapper.fetch(workoutId);
		assertEquals(workoutId, workout.getId());
		assertEquals(userId, workout.getUserId());
		assertEquals(Long.valueOf(1), workout.getDateMs());
		assertEquals(Double.valueOf(2.0), workout.getDistance());
		assertEquals(Double.valueOf(3.0), workout.getDuration());
		assertEquals("14004", workout.getPostalCode());
		assertEquals("SUNNY", workout.getWeather());
	}

	@Test
	public void should_insert() {
		final Long userId = getBasicUserId();

		final Workout newWorkout = new Workout() {
			{
				setUserId(userId);
				setDateMs(26L);
				setDistance(26.1);
				setDuration(3.5);
				setPostalCode("716");
				setWeather("SNOW");
			}
		};

		workoutMapper.insert(newWorkout);

		final List<Workout> workouts = workoutMapper.list(userId);
		assertEquals(2, workouts.size());

		final Workout workout = workoutMapper.fetch(newWorkout.getId());
		assertEquals(newWorkout.getId(), workout.getId());
		assertEquals(newWorkout.getUserId(), workout.getUserId());
		assertEquals(newWorkout.getDateMs(), workout.getDateMs());
		assertEquals(newWorkout.getDistance(), workout.getDistance());
		assertEquals(newWorkout.getDuration(), workout.getDuration());
		assertEquals(newWorkout.getPostalCode(), workout.getPostalCode());
		assertEquals(newWorkout.getWeather(), workout.getWeather());
	}

	@Test
	public void should_update() {
		final Long userId = getBasicUserId();
		final Long workoutId = workoutMapper.list(userId).get(0).getId();

		final Workout workout = workoutMapper.fetch(workoutId);
		workout.setUserId(-1L); // not updated
		workout.setDateMs(26L);
		workout.setDistance(26.1);
		workout.setDuration(3.5);
		workout.setPostalCode("716");
		workout.setWeather("SNOW");

		workoutMapper.update(workout);

		final List<Workout> workouts = workoutMapper.list(userId);
		assertEquals(1, workouts.size());

		final Workout updatedWorkout = workoutMapper.fetch(workoutId);
		assertEquals(workout.getId(), updatedWorkout.getId());
		assertEquals(userId, updatedWorkout.getUserId());
		assertEquals(workout.getDateMs(), updatedWorkout.getDateMs());
		assertEquals(workout.getDistance(), updatedWorkout.getDistance());
		assertEquals(workout.getDuration(), updatedWorkout.getDuration());
		assertEquals(workout.getPostalCode(), updatedWorkout.getPostalCode());
		assertEquals(workout.getWeather(), updatedWorkout.getWeather());
	}

	@Test
	public void should_delete() {
		final Long userId = getBasicUserId();
		final Long workoutId = workoutMapper.list(userId).get(0).getId();

		workoutMapper.delete(workoutId);

		final List<Workout> workouts = workoutMapper.list(userId);
		assertEquals(0, workouts.size());

		final Workout deletedWorkout = workoutMapper.fetch(workoutId);
		assertNull(deletedWorkout);
	}

	@Test
	public void should_casscade_delete() {
		final Long userId = getBasicUserId();

		userMapper.delete(userId);

		final List<Workout> workouts = workoutMapper.list(userId);
		assertEquals(0, workouts.size());
	}

	private Long getBasicUserId() {
		final User basicUser = userMapper.list().get(1);
		assertEquals("basic user", basicUser.getLogin());
		return basicUser.getId();
	}

}
