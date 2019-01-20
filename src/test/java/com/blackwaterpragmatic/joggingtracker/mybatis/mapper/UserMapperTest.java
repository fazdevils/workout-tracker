package com.blackwaterpragmatic.joggingtracker.mybatis.mapper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

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
		final List<User> users = userMapper.list();

		assertEquals(4, users.size());

		final User user = users.get(0);
		assertNotNull(user.getId());
		assertEquals("admin", user.getLogin());
		assertEquals(Integer.valueOf(4), user.getBitwiseRole());
		assertNull(user.getPassword());
		assertNull(user.getRoles());
	}

	@Test
	public void should_fetch() {
		final Long userId = getFirstUserId();

		final User user = userMapper.fetch(userId);

		assertEquals(userId, user.getId());
		assertEquals("admin", user.getLogin());
		assertEquals(Integer.valueOf(4), user.getBitwiseRole());
		assertNull(user.getPassword());
		assertNull(user.getRoles());
	}

	@Test
	public void should_fetch_password() {
		final String password = userMapper.fetchPassword("admin");

		assertEquals("LrLA9dHa+KkUIZhWexi4ng7/Sph9apKJUdVtpaTrHNayJRrc", password);
	}

	@Test
	public void should_insert() {
		final User newUser = new User() {
			{
				setBitwiseRole(0);
				setId(null);
				setLogin("newUser");
				setPassword("password");
			}
		};

		userMapper.insert(newUser);

		assertNotNull(newUser.getId());

		assertEquals(5, userMapper.list().size());

		final User user = userMapper.fetch(newUser.getId());

		assertEquals(newUser.getId(), user.getId());
		assertEquals(newUser.getLogin(), user.getLogin());
		assertEquals(newUser.getBitwiseRole(), user.getBitwiseRole());
		assertEquals(newUser.getPassword(), userMapper.fetchPassword(newUser.getLogin()));
		assertNull(user.getRoles());
	}

	@Test
	public void should_update() {
		final Long userId = getFirstUserId();

		final User updatedUser = new User() {
			{
				setBitwiseRole(0);
				setId(userId);
				setLogin("updatedUser");
				setPassword("updatedPassword"); // will not update
			}
		};

		userMapper.update(updatedUser);
		final User user = userMapper.fetch(userId);

		assertEquals(updatedUser.getId(), user.getId());
		assertEquals(updatedUser.getLogin(), user.getLogin());
		assertEquals(updatedUser.getBitwiseRole(), user.getBitwiseRole());
		assertNotEquals(updatedUser.getPassword(), userMapper.fetchPassword(updatedUser.getLogin()));
		assertNull(user.getRoles());
	}

	@Test
	public void should_update_password() {
		final Long userId = getFirstUserId();
		final String updatedPassword = "updatedPassword";

		userMapper.updatePassword(userId, updatedPassword);

		final String password = userMapper.fetchPassword("admin");

		assertEquals(updatedPassword, password);
	}

	@Test
	public void should_delete() {
		final Long userId = getFirstUserId();

		userMapper.delete(userId);

		assertEquals(3, userMapper.list().size());
	}

	private Long getFirstUserId() {
		return userMapper.list().get(0).getId();
	}
}
