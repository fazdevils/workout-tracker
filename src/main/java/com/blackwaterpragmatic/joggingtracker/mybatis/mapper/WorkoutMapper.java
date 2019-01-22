package com.blackwaterpragmatic.joggingtracker.mybatis.mapper;

import com.blackwaterpragmatic.joggingtracker.bean.Workout;

import java.util.List;

public interface WorkoutMapper {

	List<Workout> list(Long userId);

	Workout fetch(Long id);

	void insert(Workout workout);

	void update(Workout workout);

	void delete(Long id);

}
