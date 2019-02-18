package com.blackwaterpragmatic.workouttracker.helper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.blackwaterpragmatic.workouttracker.helper.ExternalWebServiceHelper;
import com.blackwaterpragmatic.workouttracker.test.MockHelper;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

@RunWith(MockitoJUnitRunner.class)
public class ExternalWebServiceHelperTest {

	@Mock
	private HttpURLConnection urlConnection;

	private final ExternalWebServiceHelper externalWebServiceHelper = new ExternalWebServiceHelper();

	private final URLStreamHandler stubUrlHandler = new URLStreamHandler() {
		@Override
		protected URLConnection openConnection(final URL u) throws IOException {
			return urlConnection;
		}
	};

	@Test
	public void should_get_web_service_response() throws IOException {
		final URL url = new URL("http", "localhost", 80, "weather", stubUrlHandler);
		final String expectedResponse = "test web service response";
		final ByteArrayInputStream inputStream = new ByteArrayInputStream(expectedResponse.getBytes());

		when(urlConnection.getResponseCode()).thenReturn(HttpURLConnection.HTTP_OK);
		when(urlConnection.getInputStream()).thenReturn(inputStream);

		final String response = externalWebServiceHelper.get(url);

		verify(urlConnection).setRequestMethod("GET");
		verify(urlConnection).getResponseCode();
		verify(urlConnection).getInputStream();
		verifyNoMoreInteractions(MockHelper.allDeclaredMocks(this));

		assertEquals(expectedResponse, response);
	}

	@Test
	public void should_return_null_on_io_error() throws IOException {
		final URL url = new URL("http", "localhost", 80, "weather", stubUrlHandler);

		when(urlConnection.getResponseCode()).thenThrow(new IOException());

		final String response = externalWebServiceHelper.get(url);

		verify(urlConnection).setRequestMethod("GET");
		verify(urlConnection).getResponseCode();
		verifyNoMoreInteractions(MockHelper.allDeclaredMocks(this));

		assertNull(response);
	}

	@Test
	public void should_return_null_on_non_200_response() throws IOException {
		final URL url = new URL("http", "localhost", 80, "weather", stubUrlHandler);

		when(urlConnection.getResponseCode()).thenReturn(HttpURLConnection.HTTP_BAD_REQUEST);

		final String response = externalWebServiceHelper.get(url);

		verify(urlConnection).setRequestMethod("GET");
		verify(urlConnection, times(2)).getResponseCode();
		verifyNoMoreInteractions(MockHelper.allDeclaredMocks(this));

		assertNull(response);
	}
}
