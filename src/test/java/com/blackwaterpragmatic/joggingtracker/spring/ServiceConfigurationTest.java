package com.blackwaterpragmatic.joggingtracker.spring;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.jasypt.digest.config.DigesterConfig;
import org.jasypt.util.password.PasswordEncryptor;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
@ContextConfiguration(classes = {ServiceConfiguration.class})
@Component
public class ServiceConfigurationTest {

	@Autowired
	private DigesterConfig digesterConfig;

	@Autowired
	private PasswordEncryptor passwordEncryptor;

	@Test
	public void should_instantiate_digester() {
		assertEquals("SHA-1", digesterConfig.getAlgorithm());
		assertEquals(2319, digesterConfig.getIterations().intValue());
		assertNotNull(digesterConfig.getSaltGenerator());
		assertEquals(16, digesterConfig.getSaltSizeBytes().intValue());
	}

	@Test
	public void should_instantiate_password_encryptor() {
		assertNotNull(passwordEncryptor);
		System.out.println(passwordEncryptor.encryptPassword("password"));
	}


}
