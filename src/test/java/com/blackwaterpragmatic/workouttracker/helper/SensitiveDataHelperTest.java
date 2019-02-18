package com.blackwaterpragmatic.workouttracker.helper;

import static org.junit.Assert.assertEquals;

import com.blackwaterpragmatic.workouttracker.helper.SensitiveDataHelper;

import org.junit.Test;

public class SensitiveDataHelperTest {

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
	public void should_redact_sensitive_data2() {
		final String requestBody = "{\"username\": \"testuser\", \"password\": \"password\"," +
				"\"PasswordHere\": \"testpasswordHere\"," +
				" \"ftp\": [{\"ftpusername\": \"ftpuserPassword\"}, { \"ftpPassword\": \"ftppassword123\"}]}";

		final String expectedMaskedRequestBody = "{\"username\": \"testuser\", \"password\": \"### REDACTED ###\"," +
				"\"PasswordHere\": \"### REDACTED ###\"," +
				" \"ftp\": [{\"ftpusername\": \"ftpuserPassword\"}, { \"ftpPassword\": \"### REDACTED ###\"}]}";

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
