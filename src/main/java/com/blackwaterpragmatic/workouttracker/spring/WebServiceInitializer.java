package com.blackwaterpragmatic.workouttracker.spring;

import com.blackwaterpragmatic.workouttracker.filter.RequestFilter;

import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;
import javax.servlet.ServletRegistration;

public class WebServiceInitializer implements WebApplicationInitializer {

	@Override
	public void onStartup(final ServletContext servletContext) {
		final AnnotationConfigWebApplicationContext springContext = new AnnotationConfigWebApplicationContext();
		springContext.setConfigLocation("com.blackwaterpragmatic.workouttracker.spring");

		addFilters(servletContext);
		addListeners(servletContext, springContext);
		addServlets(servletContext, springContext);
	}

	private void addFilters(final ServletContext servletContext) {
		final FilterRegistration.Dynamic requestFilter = servletContext.addFilter("RequestFilter", RequestFilter.class);
		requestFilter.addMappingForUrlPatterns(null, true, "/*");
	}

	private void addListeners(final ServletContext servletContext, final AnnotationConfigWebApplicationContext springContext) {
		servletContext.addListener(new ContextLoaderListener(springContext));
	}

	private void addServlets(final ServletContext servletContext, final AnnotationConfigWebApplicationContext springContext) {
		final ServletRegistration.Dynamic dispatcher = servletContext.addServlet("Dispatcher", new DispatcherServlet(springContext));
		dispatcher.setLoadOnStartup(1);
		dispatcher.addMapping("/");
	}

}
