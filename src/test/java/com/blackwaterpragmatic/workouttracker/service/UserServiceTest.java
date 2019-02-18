package com.blackwaterpragmatic.workouttracker.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.blackwaterpragmatic.workouttracker.bean.Credentials;
import com.blackwaterpragmatic.workouttracker.bean.NewUser;
import com.blackwaterpragmatic.workouttracker.bean.User;
import com.blackwaterpragmatic.workouttracker.constant.Role;
import com.blackwaterpragmatic.workouttracker.mybatis.mapper.UserMapper;
import com.blackwaterpragmatic.workouttracker.service.TokenService;
import com.blackwaterpragmatic.workouttracker.service.UserService;
import com.blackwaterpragmatic.workouttracker.test.MockHelper;

import org.jasypt.util.password.PasswordEncryptor;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceTest {

	@Mock
	private TokenService tokenService;

	@Mock
	private UserMapper userMapper;

	@Mock
	private PasswordEncryptor passwordEncryptor;

	@Mock
	private HttpServletRequest request;

	@InjectMocks
	private UserService userService;

	@Test
	public void should_list_users() {
		final User expectedUser = new User() {
			{
				setBitwiseRole(1);
			}
		};
		final List<User> expectedUsers = new ArrayList<User>() {
			{
				add(expectedUser);
			}
		};

		assertNull(expectedUser.getRoles());

		when(userMapper.list(null, null)).thenReturn(expectedUsers);

		final List<User> users = userService.listUsers(null, null);

		verify(userMapper).list(null, null);
		verifyNoMoreInteractions(MockHelper.allDeclaredMocks(this));

		assertEquals(expectedUsers, users);
		assertEquals(expectedUser, users.get(0));
		assertEquals(1, users.get(0).getRoles().size());
		assertEquals(Role.USER, users.get(0).getRoles().iterator().next());
	}

	@Test
	public void should_fetch_user() {
		final Long userId = 1L;
		final User expectedUser = new User() {
			{
				setBitwiseRole(1);
			}
		};

		when(userMapper.fetch(userId)).thenReturn(expectedUser);

		final User user = userService.getUser(userId);

		verify(userMapper).fetch(userId);
		verifyNoMoreInteractions(MockHelper.allDeclaredMocks(this));

		assertEquals(expectedUser, user);
		assertEquals(1, expectedUser.getRoles().size());
		assertEquals(Role.USER, expectedUser.getRoles().iterator().next());
	}

	@Test
	public void should_not_fetch_missing_user() {
		final Long userId = 1L;

		final User user = userService.getUser(userId);

		verify(userMapper).fetch(userId);
		verifyNoMoreInteractions(MockHelper.allDeclaredMocks(this));

		assertNull(user);
	}

	@Test
	public void should_register_user() {
		final Long userId = 1L;
		final NewUser newUser = new NewUser() {
			{
				setId(userId); // will be set on insert
				setPassword("password");
				setRoles(new HashSet<Role>() {
					{
						add(Role.USER_MANAGER);
					}
				});
			}
		};

		when(passwordEncryptor.encryptPassword(newUser.getPassword())).thenReturn("encryptedPassword");

		userService.registerUser(newUser, false);

		verify(passwordEncryptor).encryptPassword("password");
		verify(userMapper).insert(newUser);
		verify(userMapper).fetch(userId);
		verifyNoMoreInteractions(MockHelper.allDeclaredMocks(this));

		assertEquals("encryptedPassword", newUser.getPassword());
		assertEquals(Role.USER.getBitwisePermission(), newUser.getBitwiseRole().intValue());
	}

	@Test
	public void should_register_user_for_manager() {
		final Long userId = 1L;
		final NewUser newUser = new NewUser() {
			{
				setId(userId); // will be set on insert
				setPassword("password");
				setRoles(new HashSet<Role>() {
					{
						add(Role.USER_MANAGER);
					}
				});
			}
		};

		when(passwordEncryptor.encryptPassword(newUser.getPassword())).thenReturn("encryptedPassword");

		userService.registerUser(newUser, true);

		verify(passwordEncryptor).encryptPassword("password");
		verify(userMapper).insert(newUser);
		verify(userMapper).fetch(userId);
		verifyNoMoreInteractions(MockHelper.allDeclaredMocks(this));

		assertEquals("encryptedPassword", newUser.getPassword());
		assertEquals(Role.USER_MANAGER.getBitwisePermission(), newUser.getBitwiseRole().intValue());
	}

	@Test
	public void should_register_admin_user_for_manager() {
		final Long userId = 1L;
		final NewUser newUser = new NewUser() {
			{
				setId(userId); // will be set on insert
				setPassword("password");
				setRoles(new HashSet<Role>() {
					{
						add(Role.ADMIN);
					}
				});
			}
		};

		when(passwordEncryptor.encryptPassword(newUser.getPassword())).thenReturn("encryptedPassword");

		userService.registerUser(newUser, true);

		verify(passwordEncryptor).encryptPassword("password");
		verify(userMapper).insert(newUser);
		verify(userMapper).fetch(userId);
		verifyNoMoreInteractions(MockHelper.allDeclaredMocks(this));

		assertEquals("encryptedPassword", newUser.getPassword());

		// admins have all privileges
		assertEquals(Role.USER.getBitwisePermission() |
				Role.USER_MANAGER.getBitwisePermission() |
				Role.ADMIN.getBitwisePermission(),
				newUser.getBitwiseRole().intValue());
	}

	@Test
	public void should_update_user() {
		final Long userId = 1L;
		final User updatedUser = new User() {
			{
				setId(userId);
				setBitwiseRole(Role.USER.getBitwisePermission());
				setRoles(new HashSet<Role>() {
					{
						add(Role.USER_MANAGER);
					}
				});
			}
		};

		userService.updateUser(updatedUser, false);

		verify(userMapper).update(updatedUser, false);
		verify(userMapper).fetch(userId);
		verifyNoMoreInteractions(MockHelper.allDeclaredMocks(this));

		assertEquals(Role.USER.getBitwisePermission(), updatedUser.getBitwiseRole().intValue());
	}

	@Test
	public void should_update_user_for_manager() {
		final Long userId = 1L;
		final User updatedUser = new User() {
			{
				setId(userId);
				setBitwiseRole(Role.USER.getBitwisePermission());
				setRoles(new HashSet<Role>() {
					{
						add(Role.USER_MANAGER);
					}
				});
			}
		};

		userService.updateUser(updatedUser, true);

		verify(userMapper).update(updatedUser, true);
		verify(userMapper).fetch(userId);
		verifyNoMoreInteractions(MockHelper.allDeclaredMocks(this));

		assertEquals(Role.USER_MANAGER.getBitwisePermission(), updatedUser.getBitwiseRole().intValue());
	}

	@Test
	public void should_update_password() {
		final Long userId = 1L;
		final String password = "password";

		when(passwordEncryptor.encryptPassword(password)).thenReturn("encryptedPassword");

		userService.updatePassword(userId, password);

		verify(passwordEncryptor).encryptPassword(password);
		verify(userMapper).updatePassword(userId, "encryptedPassword");
		verifyNoMoreInteractions(MockHelper.allDeclaredMocks(this));
	}

	@Test
	public void should_delete() {
		final Long userId = 1L;

		userService.delete(userId);

		verify(userMapper).delete(userId);
		verifyNoMoreInteractions(MockHelper.allDeclaredMocks(this));
	}

	@Test
	public void should_activate() {
		final Long userId = 1L;

		userService.activate(userId);

		verify(userMapper).activate(userId);
		verifyNoMoreInteractions(MockHelper.allDeclaredMocks(this));
	}

	@Test
	public void should_deactivate() {
		final Long userId = 1L;

		userService.deactivate(userId);

		verify(userMapper).deactivate(userId);
		verifyNoMoreInteractions(MockHelper.allDeclaredMocks(this));
	}

	@Test
	public void should_build_authentication_token() {
		final Long userId = 1L;
		final Credentials credentials = new Credentials() {
			{
				setLogin("login");
				setPassword("password");
			}
		};

		final Credentials storedCredentials = new Credentials() {
			{
				setUserId(userId);
				setLogin("login");
				setPassword("encryptedPassword");
			}
		};

		final User user = new User();

		when(userMapper.fetchStoredCredentials("login")).thenReturn(storedCredentials);
		when(passwordEncryptor.checkPassword("password", "encryptedPassword")).thenReturn(true);
		when(userMapper.fetch(userId)).thenReturn(user);
		when(tokenService.buildAuthenticationToken(user, "userAgent", request)).thenReturn("token");

		final String token = userService.buildAuthenticationToken(credentials, "userAgent", request);

		verify(userMapper).fetchStoredCredentials("login");
		verify(passwordEncryptor).checkPassword("password", "encryptedPassword");
		verify(userMapper).fetch(userId);
		verify(tokenService).buildAuthenticationToken(user, "userAgent", request);
		verifyNoMoreInteractions(MockHelper.allDeclaredMocks(this));

		assertEquals("token", token);
	}

	@Test
	public void should_not_build_authentication_token_bad_password() {
		final Long userId = 1L;
		final Credentials credentials = new Credentials() {
			{
				setLogin("login");
				setPassword("password");
			}
		};

		final Credentials storedCredentials = new Credentials() {
			{
				setUserId(userId);
				setLogin("login");
				setPassword("encryptedPassword");
			}
		};

		when(userMapper.fetchStoredCredentials("login")).thenReturn(storedCredentials);
		when(passwordEncryptor.checkPassword("password", "encryptedPassword")).thenReturn(false);

		final String token = userService.buildAuthenticationToken(credentials, "userAgent", request);

		verify(userMapper).fetchStoredCredentials("login");
		verify(passwordEncryptor).checkPassword("password", "encryptedPassword");
		verifyNoMoreInteractions(MockHelper.allDeclaredMocks(this));

		assertNull(token);
	}


	@Test
	public void should_not_build_authentication_token_bad_user() {
		final Credentials credentials = new Credentials() {
			{
				setLogin("login");
				setPassword("password");
			}
		};

		final String token = userService.buildAuthenticationToken(credentials, "userAgent", request);

		verify(userMapper).fetchStoredCredentials("login");
		verifyNoMoreInteractions(MockHelper.allDeclaredMocks(this));

		assertNull(token);
	}
}
