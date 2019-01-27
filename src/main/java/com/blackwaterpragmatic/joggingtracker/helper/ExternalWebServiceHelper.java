package com.blackwaterpragmatic.joggingtracker.helper;

import static org.apache.logging.log4j.LogManager.getLogger;

import com.blackwaterpragmatic.joggingtracker.service.WorkoutService;

import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

@Component
public class ExternalWebServiceHelper {

	private static final Logger log = getLogger(WorkoutService.class);

	public String get(final URL url) {
		try {
			final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");
			final StringBuilder response = new StringBuilder();
			if (HttpURLConnection.HTTP_OK == connection.getResponseCode()) {
				try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
					reader.lines().forEach(response::append);
				}
				return response.toString();
			} else {
				log.error(String.format("Invalid (%d) response from %s.", connection.getResponseCode(), url.toString()));
				response.append("{}");
			}
		} catch (final Exception e) {
			log.error(e.getMessage(), e);
		}
		return null;
	}

}
