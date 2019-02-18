package com.blackwaterpragmatic.workouttracker.spring;

import com.blackwaterpragmatic.workouttracker.bean.internal.ApplicationEnvironment;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.core.env.Environment;

import java.net.InetAddress;
import java.net.UnknownHostException;

import io.swagger.jaxrs.config.BeanConfig;

@Configuration
@PropertySources({
		@PropertySource(value = "classpath:application-${ENV?:local}.properties")
})
public class EnvironmentConfiguration {

	@Bean
	public ApplicationEnvironment applicationEnvironment(
			final Environment environment) {

		org.apache.ibatis.logging.LogFactory.useLog4J2Logging();
		try {
			System.setProperty("serverAddress", InetAddress.getLocalHost().getHostAddress()); // used for logging
		} catch (final UnknownHostException e) {
			e.printStackTrace();
		}

		final ApplicationEnvironment applicationEnvironment = new ApplicationEnvironment();
		applicationEnvironment.setJwtSignature(environment.getProperty("jwt_signature"));
		applicationEnvironment.setJwtExpirationHours(Integer.valueOf(environment.getProperty("jwt_expiration_hours")));
		applicationEnvironment.setJwtExpirationMinutes(Integer.valueOf(environment.getProperty("jwt_expiration_minutes")));
		applicationEnvironment.setApiVersion(environment.getProperty("api_version"));
		applicationEnvironment.setApiSchemes(environment.getProperty("api_schemes"));
		applicationEnvironment.setApiHost(environment.getProperty("api_host"));
		applicationEnvironment.setApiTitle(environment.getProperty("api_title"));
		final String env = System.getProperty("ENV");
		applicationEnvironment.setEnv(null == env ? "local" : env);
		applicationEnvironment.setWeatherUrl(environment.getProperty("weather_url"));
		applicationEnvironment.setBuildTime(environment.getProperty("build_time"));
		return applicationEnvironment;
	}

	@Bean
	public BeanConfig swaggerApiConfig(final ApplicationEnvironment applicationEnvironment) {
		final BeanConfig beanConfig = new BeanConfig();
		beanConfig.setVersion(applicationEnvironment.getApiVersion());
		beanConfig.setSchemes(applicationEnvironment.getApiSchemes().split(","));
		beanConfig.setHost(applicationEnvironment.getApiHost());
		beanConfig.setTitle(applicationEnvironment.getApiTitle());
		beanConfig.setBasePath("/");
		beanConfig.setResourcePackage("com.blackwaterpragmatic.workouttracker.resource,com.blackwaterpragmatic.workouttracker.swagger");
		beanConfig.setScan(true);
		return beanConfig;
	}

}
