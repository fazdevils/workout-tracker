package com.blackwaterpragmatic.joggingtracker.spring;

import org.jasypt.digest.config.DigesterConfig;
import org.jasypt.digest.config.SimpleDigesterConfig;
import org.jasypt.salt.RandomSaltGenerator;
import org.jasypt.util.password.ConfigurablePasswordEncryptor;
import org.jasypt.util.password.PasswordEncryptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(value = "com.blackwaterpragmatic.joggingtracker")
public class ServiceConfiguration {

	private static final int SALT_SIZE = 16;
	private static final int ITERATIONS = 2319;

	@Bean
	public DigesterConfig digesterConfig() {
		final SimpleDigesterConfig digesterConfig = new SimpleDigesterConfig();
		digesterConfig.setAlgorithm("SHA-1");
		digesterConfig.setIterations(ITERATIONS);
		digesterConfig.setSaltGenerator(new RandomSaltGenerator());
		digesterConfig.setSaltSizeBytes(SALT_SIZE);

		return digesterConfig;
	}

	@Bean
	public PasswordEncryptor passwordEncryptor(final DigesterConfig digesterConfig) {
		final ConfigurablePasswordEncryptor encryptor = new ConfigurablePasswordEncryptor();
		encryptor.setConfig(digesterConfig);

		return encryptor;
	}

}
