package com.blackwaterpragmatic.joggingtracker.helper;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class SensitiveDateHelperTest {

	private final SensitiveDataHelper sensitiveDataHelper = new SensitiveDataHelper();

	@Test
	public void should_redact_sensitive_data() {
		final String requestBody = "{\"username\": \"testuser\", \"password\": \"testpassword\"," +
				"\"passwordHere\": \"testpasswordHere\"," +
				" \"ftp\": [{\"ftpusername\": \"ftpuser\"}, { \"ftppassword\": \"ftppassword123\"}]}";

		final String expectedMaskedRequestBody = "{\"username\": \"testuser\", \"password\": \"### REDACTED ###\"," +
				"\"passwordHere\": \"### REDACTED ###\"," +
				" \"ftp\": [{\"ftpusername\": \"ftpuser\"}, { \"ftppassword\": \"### REDACTED ###\"}]}";

		final String maskedRequestBody = sensitiveDataHelper.redactSensitiveData(requestBody);

		assertEquals(expectedMaskedRequestBody, maskedRequestBody);
	}

	@Test
	public void should_handle_empty_data() {
		final String requestBody = "";

		final String maskedRequestBody = sensitiveDataHelper.redactSensitiveData(requestBody);

		assertEquals(requestBody, maskedRequestBody);
	}

	@Test
	public void should_handle_null_data() {
		final String requestBody = null;

		final String maskedRequestBody = sensitiveDataHelper.redactSensitiveData(requestBody);

		assertEquals(requestBody, maskedRequestBody);
	}
}
