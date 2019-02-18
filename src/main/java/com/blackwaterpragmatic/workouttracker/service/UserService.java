package com.blackwaterpragmatic.workouttracker.service;

import com.blackwaterpragmatic.workouttracker.bean.Credentials;
import com.blackwaterpragmatic.workouttracker.bean.NewUser;
import com.blackwaterpragmatic.workouttracker.bean.User;
import com.blackwaterpragmatic.workouttracker.constant.Role;
import com.blackwaterpragmatic.workouttracker.mybatis.mapper.UserMapper;

import org.jasypt.util.password.PasswordEncryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

import java.util.List;

@Service
public class UserService {

	private final TokenService tokenService;
	private final UserMapper userMapper;
	private final PasswordEncryptor passwordEncryptor;

	@Autowired
	public UserService(
			final TokenService tokenService,
			final UserMapper userMapper,
			final PasswordEncryptor passwordEncryptor) {
		this.tokenService = tokenService;
		this.userMapper = userMapper;
		this.passwordEncryptor = passwordEncryptor;
	}

	public List<User> listUsers(final Integer start, final Integer max) {
		final List<User> users = userMapper.list(start, max);
		for (final User user : users) {
			user.setRoles(Role.getRoles(user.getBitwiseRole())); // TODO could probably move these calls into mybatis type handler
		}
		return users;
	}

	public User getUser(final Long userId) {
		final User user = userMapper.fetch(userId);
		if (null != user) {
			user.setRoles(Role.getRoles(user.getBitwiseRole()));
		}
		return user;
	}

	public User registerUser(final NewUser newUser, final boolean asUserManager) {
		newUser.setActive(false);
		if (asUserManager) {
			newUser.setBitwiseRole(Role.getRoles(newUser.getRoles()));
		} else {
			newUser.setBitwiseRole(Role.USER.getBitwisePermission());
		}
		newUser.setPassword(passwordEncryptor.encryptPassword(newUser.getPassword()));
		userMapper.insert(newUser);
		return getUser(newUser.getId());
	}

	public User updateUser(final User user, final boolean asUserManager) {
		if (asUserManager) {
			user.setBitwiseRole(Role.getRoles(user.getRoles()));
		}
		userMapper.update(user, asUserManager);
		return getUser(user.getId());
	}

	public void updatePassword(final Long userId, final String password) {
		userMapper.updatePassword(userId, passwordEncryptor.encryptPassword(password));
	}

	public void delete(final Long userId) {
		userMapper.delete(userId);
	}

	public void activate(final Long userId) {
		userMapper.activate(userId);
	}

	public void deactivate(final Long userId) {
		userMapper.deactivate(userId);
	}

	public String buildAuthenticationToken(final Credentials credentials, final String userAgent, final HttpServletRequest request) {
		final Credentials storedCredentials = userMapper.fetchStoredCredentials(credentials.getLogin());
		if ((null != storedCredentials) && validPassword(credentials.getPassword(), storedCredentials.getPassword())) {
			return tokenService.buildAuthenticationToken(userMapper.fetch(storedCredentials.getUserId()), userAgent, request);
		}
		return null;
	}

	private Boolean validPassword(final String password, final String encryptedPassword) {
		return passwordEncryptor.checkPassword(password, encryptedPassword);
	}
}
