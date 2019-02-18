package com.blackwaterpragmatic.workouttracker.helper;

import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class SensitiveDataHelper {

	private static final Pattern PASSWORD_PATTERN = Pattern.compile("\"\\w*(?i)password\\w*\":\\s?\"([^\"]+)\"");
	private static final String SENSITIVE_DATA_MESSAGE = "### REDACTED ###";

	public String redactSensitiveData(final String requestBody) {
		if (null == requestBody) {
			return null;
		} else {
			final Matcher matcher = PASSWORD_PATTERN.matcher(requestBody);
			final StringBuffer maskedRequestBody = new StringBuffer();

			while (matcher.find()) {
				final String redactedMatch = String.format("%s%s%s\"",
						requestBody.substring(matcher.start(0), matcher.start(1)),
						SENSITIVE_DATA_MESSAGE,
						requestBody.substring(matcher.end(1), matcher.end(1)));
				matcher.appendReplacement(maskedRequestBody, redactedMatch);

			}
			matcher.appendTail(maskedRequestBody); // append the rest of the contents
			return maskedRequestBody.toString();
		}
	}

}
