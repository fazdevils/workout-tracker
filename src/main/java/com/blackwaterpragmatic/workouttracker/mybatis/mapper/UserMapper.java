package com.blackwaterpragmatic.workouttracker.mybatis.mapper;

import com.blackwaterpragmatic.workouttracker.bean.Credentials;
import com.blackwaterpragmatic.workouttracker.bean.NewUser;
import com.blackwaterpragmatic.workouttracker.bean.User;

import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UserMapper {

	List<User> list(@Param("start") Integer start, @Param("max") Integer max);

	User fetch(Long userId);

	Credentials fetchStoredCredentials(String login);

	void insert(NewUser user);

	void update(@Param("user") User user, @Param("asUserManager") Boolean asUserManager);

	void updatePassword(@Param("userId") Long userId, @Param("password") String password);

	void delete(Long userId);

	void activate(Long userId);

	void deactivate(Long userId);

}
