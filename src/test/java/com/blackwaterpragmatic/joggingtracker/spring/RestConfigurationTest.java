package com.blackwaterpragmatic.joggingtracker.spring;

import static org.junit.Assert.assertNotNull;

import org.jboss.resteasy.spi.ResteasyDeployment;
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
@ContextConfiguration(classes = {RestConfiguration.class})
@Component
public class RestConfigurationTest {

	@Autowired
	private ResteasyDeployment resteasyDeployment;

	@Test
	public void should_initialize_resteasy() {
		assertNotNull(resteasyDeployment);
	}

}
