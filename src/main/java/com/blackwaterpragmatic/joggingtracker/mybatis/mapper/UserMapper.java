package com.blackwaterpragmatic.joggingtracker.mybatis.mapper;

import com.blackwaterpragmatic.joggingtracker.bean.User;

import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UserMapper {

	List<User> list();

	User fetch(Long userId);

	String fetchPassword(String login);

	void insert(User user);

	void update(User user);

	void updatePassword(@Param("userId") Long userId, @Param("password") String password);

	void delete(Long userId);
}
