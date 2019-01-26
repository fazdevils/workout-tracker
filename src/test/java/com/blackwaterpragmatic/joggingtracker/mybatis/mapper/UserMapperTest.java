package com.blackwaterpragmatic.joggingtracker.mybatis.mapper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.blackwaterpragmatic.joggingtracker.bean.Credentials;
import com.blackwaterpragmatic.joggingtracker.bean.NewUser;
import com.blackwaterpragmatic.joggingtracker.bean.User;
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
public class UserMapperTest {

	@Autowired
	private UserMapper userMapper;

	@Test
	public void should_list_users() {
		final List<User> users = userMapper.list(null, null);

		assertEquals(4, users.size());

		final User user = users.get(0);
		assertNotNull(user.getId());
		assertEquals("admin", user.getLogin());
		assertEquals(Integer.valueOf(4), user.getBitwiseRole());
		assertNull(user.getRoles());
		assertTrue(user.getActive());
	}

	@Test
	public void should_list_selected_users() {
		final List<User> users = userMapper.list(0, 5);

		assertEquals(4, users.size());

		final User user = users.get(0);
		assertNotNull(user.getId());
		assertEquals("admin", user.getLogin());
		assertEquals(Integer.valueOf(4), user.getBitwiseRole());
		assertNull(user.getRoles());
		assertTrue(user.getActive());

		final List<User> moreUsers = userMapper.list(5, 5);
		assertEquals(0, moreUsers.size());

		final List<User> fewerUsers = userMapper.list(1, 5);
		assertEquals(3, fewerUsers.size());
	}

	@Test
	public void should_fetch() {
		final Long userId = getFirstUserId();

		final User user = userMapper.fetch(userId);

		assertEquals(userId, user.getId());
		assertEquals("admin", user.getLogin());
		assertEquals(Integer.valueOf(4), user.getBitwiseRole());
		assertNull(user.getRoles());
		assertTrue(user.getActive());
	}

	@Test
	public void should_fetch_password() {
		final Credentials credentials = userMapper.fetchStoredCredentials("admin");

		assertNotNull(credentials.getUserId());
		assertEquals("admin", credentials.getLogin());
		assertEquals("LrLA9dHa+KkUIZhWexi4ng7/Sph9apKJUdVtpaTrHNayJRrc", credentials.getPassword());
	}

	@Test
	public void should_insert() {
		final NewUser newUser = new NewUser() {
			{
				setBitwiseRole(0);
				setId(null);
				setLogin("newUser");
				setPassword("password");
				setActive(true);
			}
		};

		userMapper.insert(newUser);

		assertNotNull(newUser.getId());

		assertEquals(5, userMapper.list(null, null).size());

		final User user = userMapper.fetch(newUser.getId());

		assertEquals(newUser.getId(), user.getId());
		assertEquals(newUser.getLogin(), user.getLogin());
		assertEquals(newUser.getBitwiseRole(), user.getBitwiseRole());
		assertEquals(newUser.getPassword(), userMapper.fetchStoredCredentials(user.getLogin()).getPassword());
		assertNull(user.getRoles());
		assertTrue(user.getActive());
	}

	@Test
	public void should_update_for_non_manager() {
		final Long userId = getFirstUserId();

		final User updatedUser = new User() {
			{
				setBitwiseRole(0); // will not update
				setId(userId);
				setLogin("updatedUser");
				setActive(false); // will not update
			}
		};

		userMapper.update(updatedUser, false);
		final User user = userMapper.fetch(userId);

		assertEquals(updatedUser.getId(), user.getId());
		assertEquals(userId, user.getId());
		assertEquals(updatedUser.getLogin(), user.getLogin());
		assertNotEquals(updatedUser.getBitwiseRole(), user.getBitwiseRole());
		assertNull(user.getRoles());
		assertNotEquals(updatedUser.getActive(), user.getActive());
	}

	@Test
	public void should_update_for_manager() {
		final Long userId = getFirstUserId();

		final User updatedUser = new User() {
			{
				setBitwiseRole(0);
				setId(userId);
				setLogin("updatedUser");
				setActive(false);
			}
		};

		userMapper.update(updatedUser, true);
		final User user = userMapper.fetch(userId);

		assertEquals(updatedUser.getId(), user.getId());
		assertEquals(userId, user.getId());
		assertEquals(updatedUser.getLogin(), user.getLogin());
		assertEquals(updatedUser.getBitwiseRole(), user.getBitwiseRole());
		assertNull(user.getRoles());
		assertEquals(updatedUser.getActive(), user.getActive());
	}

	@Test
	public void should_update_password() {
		final Long userId = getFirstUserId();
		final String updatedPassword = "updatedPassword";

		userMapper.updatePassword(userId, updatedPassword);

		final String password = userMapper.fetchStoredCredentials("admin").getPassword();

		assertEquals(updatedPassword, password);
	}

	@Test
	public void should_delete() {
		final Long userId = getFirstUserId();

		userMapper.delete(userId);

		assertEquals(3, userMapper.list(null, null).size());
	}

	@Test
	public void should_activate() {
		final NewUser newUser = new NewUser() {
			{
				setBitwiseRole(0);
				setId(null);
				setLogin("newUser");
				setPassword("password");
				setActive(false);
			}
		};

		userMapper.insert(newUser);

		final User inactiveUser = userMapper.fetch(newUser.getId());
		assertFalse(inactiveUser.getActive());

		userMapper.activate(newUser.getId());

		final User activeUser = userMapper.fetch(newUser.getId());
		assertTrue(activeUser.getActive());
	}

	@Test
	public void should_deactivate() {
		final NewUser newUser = new NewUser() {
			{
				setBitwiseRole(0);
				setId(null);
				setLogin("newUser");
				setPassword("password");
				setActive(true);
			}
		};

		userMapper.insert(newUser);

		final User activeUser = userMapper.fetch(newUser.getId());
		assertTrue(activeUser.getActive());

		userMapper.deactivate(newUser.getId());

		final User inactiveUser = userMapper.fetch(newUser.getId());
		assertFalse(inactiveUser.getActive());
	}

	private Long getFirstUserId() {
		return userMapper.list(null, null).get(0).getId();
	}
}
