package com.blackwaterpragmatic.joggingtracker.filter;

import static com.blackwaterpragmatic.joggingtracker.constant.RequestScopeAttribute.AUTHENTICATED_USER;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.blackwaterpragmatic.joggingtracker.bean.User;
import com.blackwaterpragmatic.joggingtracker.helper.SensitiveDataHelper;
import com.blackwaterpragmatic.joggingtracker.test.MockHelper;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.web.util.ContentCachingRequestWrapper;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.HttpMethod;

import java.io.IOException;

@RunWith(MockitoJUnitRunner.class)
public class RequestFilterTest {

	@Mock
	private FilterConfig filterConfig;

	@Mock
	private HttpServletRequest request;

	@Mock
	private HttpServletResponse response;

	@Mock
	private FilterChain chain;

	@Mock
	private SensitiveDataHelper helper;

	@InjectMocks
	private RequestFilter requestFilter;


	@Test
	public void should_init() {
		requestFilter.init(filterConfig);
		verifyNoMoreInteractions(MockHelper.allDeclaredMocks(this));
	}

	@Test
	public void should_destroy() {
		requestFilter.destroy();
		verifyNoMoreInteractions(MockHelper.allDeclaredMocks(this));
	}

	@Test
	public void should_log_request() throws IOException, ServletException {
		final String uriString = "/some/mock/request";
		final StringBuffer urlBuffer = new StringBuffer("https://" + uriString);

		when(request.getRequestURI()).thenReturn(uriString);
		when(request.getRequestURL()).thenReturn(urlBuffer);
		when(request.getAttribute(AUTHENTICATED_USER)).thenReturn(new User() {
			{
				setId(1L);
			}
		});

		requestFilter.doFilter(request, response, chain);

		verify(chain).doFilter(any(ContentCachingRequestWrapper.class), any(ServletResponse.class));
		verify(request).getContentLength();
		verify(request).getRequestURI();
		verify(request).getRequestURL();
		verify(request).getQueryString();
		verify(request).getAttribute(AUTHENTICATED_USER);
		verify(request).getMethod();
		verify(response).getStatus();
		verifyNoMoreInteractions(MockHelper.allDeclaredMocks(this));
	}

	@Test
	public void should_log_request_with_query_string() throws IOException, ServletException {
		final String uriString = "/some/mock/request";
		final String queryString = "param=value";
		final StringBuffer urlBuffer = new StringBuffer("https://" + uriString);

		when(request.getRequestURI()).thenReturn(uriString);
		when(request.getRequestURL()).thenReturn(urlBuffer);
		when(request.getQueryString()).thenReturn(queryString);

		requestFilter.doFilter(request, response, chain);

		verify(chain).doFilter(any(ContentCachingRequestWrapper.class), any(ServletResponse.class));
		verify(request).getContentLength();
		verify(request).getRequestURI();
		verify(request).getRequestURL();
		verify(request).getQueryString();
		verify(request).getAttribute(AUTHENTICATED_USER);
		verify(request).getMethod();
		verify(response).getStatus();
		verifyNoMoreInteractions(MockHelper.allDeclaredMocks(this));
	}

	@Test
	public void should_not_log_status_request() throws IOException, ServletException {
		final String uriString = "/status";

		when(request.getRequestURI()).thenReturn(uriString);

		requestFilter.doFilter(request, response, chain);

		verify(chain).doFilter(any(ContentCachingRequestWrapper.class), any(ServletResponse.class));
		verify(request).getContentLength();
		verify(request).getRequestURI();
		verifyNoMoreInteractions(MockHelper.allDeclaredMocks(this));
	}

	@Test
	public void should_not_log_base_request() throws IOException, ServletException {
		final String uriString = "/";

		when(request.getRequestURI()).thenReturn(uriString);

		requestFilter.doFilter(request, response, chain);

		verify(chain).doFilter(any(ContentCachingRequestWrapper.class), any(ServletResponse.class));
		verify(request).getContentLength();
		verify(request).getRequestURI();
		verifyNoMoreInteractions(MockHelper.allDeclaredMocks(this));
	}

	@Test
	public void should_log_request_body_post_request() throws IOException, ServletException {
		final String uriString = "/some/mock/request";
		final StringBuffer urlBuffer = new StringBuffer("https://" + uriString);
		final String requestBody = "{\"username\":\"newuser\"}";

		when(request.getMethod()).thenReturn(HttpMethod.POST);
		when(request.getContentLength()).thenReturn(100);
		when(request.getRequestURI()).thenReturn(uriString);
		when(request.getRequestURL()).thenReturn(urlBuffer);
		when(request.getAttribute(AUTHENTICATED_USER)).thenReturn(new User() {
			{
				setId(1L);
			}
		});
		when(helper.redactSensitiveData(any(String.class))).thenReturn(requestBody);
		requestFilter.doFilter(request, response, chain);

		verify(chain).doFilter(any(ContentCachingRequestWrapper.class), any(ServletResponse.class));
		verify(request).getContentLength();
		verify(request).getRequestURI();
		verify(request).getRequestURL();
		verify(request).getQueryString();
		verify(request).getAttribute(AUTHENTICATED_USER);
		verify(request).getMethod();
		verify(helper).redactSensitiveData(any(String.class));
		verify(response).getStatus();
		verifyNoMoreInteractions(MockHelper.allDeclaredMocks(this));
	}

	@Test
	public void should_log_request_body_put_request() throws IOException, ServletException {
		final String uriString = "/some/mock/request";
		final StringBuffer urlBuffer = new StringBuffer("https://" + uriString);
		final String requestBody = "{\"username\":\"newuser\"}";

		when(request.getMethod()).thenReturn(HttpMethod.PUT);
		when(request.getContentLength()).thenReturn(100);
		when(request.getRequestURI()).thenReturn(uriString);
		when(request.getRequestURL()).thenReturn(urlBuffer);
		when(request.getAttribute(AUTHENTICATED_USER)).thenReturn(new User() {
			{
				setId(1L);
			}
		});
		when(helper.redactSensitiveData(any(String.class))).thenReturn(requestBody);
		requestFilter.doFilter(request, response, chain);

		verify(chain).doFilter(any(ContentCachingRequestWrapper.class), any(ServletResponse.class));
		verify(request).getContentLength();
		verify(request).getRequestURI();
		verify(request).getRequestURL();
		verify(request).getQueryString();
		verify(request).getAttribute(AUTHENTICATED_USER);
		verify(request).getMethod();
		verify(helper).redactSensitiveData(any(String.class));
		verify(response).getStatus();
		verifyNoMoreInteractions(MockHelper.allDeclaredMocks(this));
	}

}
