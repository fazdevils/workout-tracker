package com.blackwaterpragmatic.joggingtracker.mybatis.mapper;

import com.blackwaterpragmatic.joggingtracker.bean.Credentials;
import com.blackwaterpragmatic.joggingtracker.bean.NewUser;
import com.blackwaterpragmatic.joggingtracker.bean.User;

import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UserMapper {

	List<User> list();

	User fetch(Long userId);

	Credentials fetchStoredCredentials(String login);

	void insert(NewUser user);

	void update(@Param("userId") Long userId, @Param("user") User user, @Param("isUserManager") Boolean isUserManager);

	void updatePassword(@Param("userId") Long userId, @Param("password") String password);

	void delete(Long userId);

	void activate(Long userId);

	void deactivate(Long userId);

}
