package com.blackwaterpragmatic.joggingtracker.service;

import com.blackwaterpragmatic.joggingtracker.bean.Credentials;
import com.blackwaterpragmatic.joggingtracker.bean.NewUser;
import com.blackwaterpragmatic.joggingtracker.bean.User;
import com.blackwaterpragmatic.joggingtracker.constant.Role;
import com.blackwaterpragmatic.joggingtracker.mybatis.mapper.UserMapper;

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

	public List<User> listUsers() {
		final List<User> users = userMapper.list();
		for (final User user : users) {
			user.setRoles(Role.getRoles(user.getBitwiseRole())); // TODO could probably make this mybatis type handler
		}
		return users;
	}

	public User getUser(final Long userId) {
		final User user = userMapper.fetch(userId);
		user.setRoles(Role.getRoles(user.getBitwiseRole())); // TODO could probably make this mybatis type handler
		return user;
	}

	public User registerUser(final NewUser newUser, final boolean isUserManager) {
		newUser.setActive(false);
		if (isUserManager) {
			newUser.setBitwiseRole(Role.getRoles(newUser.getRoles())); // TODO could probably make this mybatis type handler
		} else {
			newUser.setBitwiseRole(Role.USER.getBitwisePermission()); // TODO could probably make this mybatis type handler
		}
		newUser.setPassword(passwordEncryptor.encryptPassword(newUser.getPassword()));
		userMapper.insert(newUser);
		return getUser(newUser.getId());
	}

	public User updateUser(final Long userId, final User user, final boolean isUserManager) {
		if (isUserManager) {
			user.setBitwiseRole(Role.getRoles(user.getRoles())); // TODO could probably make this mybatis type handler
		}
		userMapper.update(userId, user, isUserManager);
		return getUser(userId);
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
		if (validPassword(credentials.getPassword(), storedCredentials.getPassword())) {
			return tokenService.buildAuthenticationToken(userMapper.fetch(credentials.getUserId()), userAgent, request);
		}
		return null;
	}

	private Boolean validPassword(final String password, final String encryptedPassword) {
		return passwordEncryptor.checkPassword(password, encryptedPassword);
	}
}
