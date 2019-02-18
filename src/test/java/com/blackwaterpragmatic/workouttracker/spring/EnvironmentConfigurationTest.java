package com.blackwaterpragmatic.workouttracker.spring;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import com.blackwaterpragmatic.workouttracker.bean.internal.ApplicationEnvironment;
import com.blackwaterpragmatic.workouttracker.spring.EnvironmentConfiguration;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import io.swagger.jaxrs.config.BeanConfig;

@RunWith(SpringJUnit4ClassRunner.class)
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
@ContextConfiguration(classes = {EnvironmentConfiguration.class})
@Component
public class EnvironmentConfigurationTest {

	@Autowired
	private Environment environment;

	@Autowired
	private ApplicationEnvironment applicationEnvironment;

	@Autowired
	private BeanConfig swaggerApiConfig;

	@Test
	public void should_use_property_placeholders_for_environment() {
		assertNotNull(System.getProperty("serverAddress"));

		assertEquals("INFO", environment.getProperty("logging.level.org.springframework"));
		assertEquals("5A3M0\".n%R\"!C/S?WHJGJ=?=", environment.getProperty("jwt_signature"));
		assertEquals("12", environment.getProperty("jwt_expiration_hours"));
		assertEquals("0", environment.getProperty("jwt_expiration_minutes"));
		assertNotNull(environment.getProperty("api_version"));
		assertEquals("http", environment.getProperty("api_schemes"));
		assertEquals("localhost:8080", environment.getProperty("api_host"));
		assertEquals("Workout Tracker API", environment.getProperty("api_title"));
		assertEquals("http://localhost:8080", environment.getProperty("weather_url"));
		assertNotNull(environment.getProperty("build_time"));

		assertEquals("5A3M0\".n%R\"!C/S?WHJGJ=?=", applicationEnvironment.getJwtSignature());
		assertEquals(12, applicationEnvironment.getJwtExpirationHours().intValue());
		assertEquals(0, applicationEnvironment.getJwtExpirationMinutes().intValue());
		assertNotNull(applicationEnvironment.getApiVersion());
		assertEquals("http", applicationEnvironment.getApiSchemes());
		assertEquals("localhost:8080", applicationEnvironment.getApiHost());
		assertEquals("Workout Tracker API", applicationEnvironment.getApiTitle());
		assertEquals("http://localhost:8080", applicationEnvironment.getWeatherUrl());
		assertEquals("local", applicationEnvironment.getEnv());
		assertNotNull(applicationEnvironment.getBuildTime());
	}

	@Test
	public void should_configure_swagger_api() {
		assertNotNull(swaggerApiConfig.getVersion());
		assertEquals(1, swaggerApiConfig.getSchemes().length);
		assertEquals("http", swaggerApiConfig.getSchemes()[0]);
		assertEquals("localhost:8080", swaggerApiConfig.getHost());
		assertEquals("Workout Tracker API", swaggerApiConfig.getTitle());
		assertEquals("/", swaggerApiConfig.getBasePath());
		assertEquals("com.blackwaterpragmatic.workouttracker.resource,com.blackwaterpragmatic.workouttracker.swagger",
				swaggerApiConfig.getResourcePackage());
		assertEquals(true, swaggerApiConfig.getScan());

	}
}
