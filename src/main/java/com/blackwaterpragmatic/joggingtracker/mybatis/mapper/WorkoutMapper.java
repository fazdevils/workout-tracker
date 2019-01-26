package com.blackwaterpragmatic.joggingtracker.mybatis.mapper;

import com.blackwaterpragmatic.joggingtracker.bean.Workout;

import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface WorkoutMapper {

	List<Workout> list(Long userId);

	Workout fetch(@Param("userId") Long userId, @Param("workoutId") Long workoutId);

	void insert(Workout workout);

	void update(Workout workout);

	void delete(@Param("userId") Long userId, @Param("workoutId") Long workoutId);

}
