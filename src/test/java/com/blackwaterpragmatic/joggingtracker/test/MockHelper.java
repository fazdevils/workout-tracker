package com.blackwaterpragmatic.joggingtracker.test;

import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.mock.MockDispatcherFactory;
import org.mockito.Mock;
import org.springframework.stereotype.Service;

import javax.ws.rs.Path;

import java.lang.reflect.Field;
import java.util.ArrayList;

public class MockHelper {

	public static Object[] allDeclaredMocks(final Object test) {

		final Field[] declaredFields = test.getClass().getDeclaredFields();

		final ArrayList<Object> mockList = new ArrayList<Object>();

		for (final Field field : declaredFields) {
			if (null != field.getAnnotation(Mock.class)) {
				field.setAccessible(true);
				try {
					mockList.add(field.get(test));
				} catch (final IllegalAccessException e) {
					throw new RuntimeException(e.toString(), e);
				}
			}
		}

		return mockList.toArray();

	}

	public static Dispatcher createMockDispatcher(final Object restResource) {
		if (restResource.getClass().isAnnotationPresent(Service.class) && restResource.getClass().isAnnotationPresent(Path.class)) {
			final Dispatcher dispatcher = MockDispatcherFactory.createDispatcher();
			dispatcher.getRegistry().addSingletonResource(restResource);
			return dispatcher;
		} else {
			throw new UnsupportedOperationException("Configuration error.  This does not seem to be a REST endpoint.");
		}
	}

}
