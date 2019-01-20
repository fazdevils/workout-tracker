package com.blackwaterpragmatic.joggingtracker.service;

import com.blackwaterpragmatic.joggingtracker.bean.User;
import com.blackwaterpragmatic.joggingtracker.mybatis.mapper.UserMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

	private final UserMapper userMapper;

	@Autowired
	public UserService(
			final UserMapper userMapper) {
		this.userMapper = userMapper;
	}

	public List<User> listUsers() {
		return userMapper.list();
	}

	public User getUser(final Long userId) {
		return userMapper.fetch(userId);
	}

}
