package com.blackwaterpragmatic.joggingtracker.helper;

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
				matcher.appendReplacement(
						maskedRequestBody,
						matcher.group(0).replaceFirst(Pattern.quote(matcher.group(1)), SENSITIVE_DATA_MESSAGE));
			}
			matcher.appendTail(maskedRequestBody); // append the rest of the contents
			return maskedRequestBody.toString();
		}
	}

}
