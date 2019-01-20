package com.blackwaterpragmatic.joggingtracker.filter;

import static com.blackwaterpragmatic.joggingtracker.constant.RequestScopeAttribute.AUTHENTICATED_USER;
import static org.apache.logging.log4j.LogManager.getLogger;

import com.blackwaterpragmatic.joggingtracker.bean.User;
import com.blackwaterpragmatic.joggingtracker.bean.internal.RequestStatistic;
import com.blackwaterpragmatic.joggingtracker.helper.SensitiveDataHelper;
import com.blackwaterpragmatic.joggingtracker.provider.AuthorizationProvider;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import org.apache.logging.log4j.Logger;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;
import org.springframework.web.util.ContentCachingRequestWrapper;

import javax.inject.Inject;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.HttpMethod;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public final class RequestFilter implements Filter {

	private static final Logger log = getLogger(AuthorizationProvider.class);
	private static final Set<String> HTTP_UPDATE_METHODS = new HashSet<String>() {
		{
			add(HttpMethod.POST);
			add(HttpMethod.PUT);
		}
	};

	@Inject
	private SensitiveDataHelper sensitiveDataHelper;

	private final ObjectWriter objectWriter = new ObjectMapper().writer();

	@Override
	public void init(final FilterConfig filterConfig) {
		SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);
	}

	@Override
	public void destroy() {
	}

	@Override
	public void doFilter(
			final ServletRequest request,
			final ServletResponse response,
			final FilterChain chain)
			throws IOException, ServletException {

		final HttpServletRequest httpRequest = (HttpServletRequest) request;
		final ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(httpRequest);

		final long startTime = System.currentTimeMillis();
		chain.doFilter(requestWrapper, response);
		final long endTime = System.currentTimeMillis();

		final HttpServletResponse httpResponse = (HttpServletResponse) response;
		final String requestURI = httpRequest.getRequestURI();

		if (shouldLogRequest(requestURI)) {
			logRequest(httpRequest, requestWrapper, httpResponse, startTime, endTime);
		}
	}

	private boolean shouldLogRequest(final String requestURI) {
		return !requestURI.equalsIgnoreCase("/") &&
				!requestURI.equalsIgnoreCase("/status");
	}

	private void logRequest(
			final HttpServletRequest httpRequest,
			final ContentCachingRequestWrapper requestWrapper,
			final HttpServletResponse httpResponse,
			final long startTime,
			final long endTime) throws JsonProcessingException {

		final String requestUrl = getRequestUrl(httpRequest);
		final String requestMethod = httpRequest.getMethod();

		final RequestStatistic requestStatistic = new RequestStatistic();
		requestStatistic.setMethod(requestMethod);
		requestStatistic.setUrl(requestUrl);
		requestStatistic.setResponseCode(httpResponse.getStatus());
		requestStatistic.setElapsedTime(endTime - startTime);

		final User authenticatedUser = (User) httpRequest.getAttribute(AUTHENTICATED_USER);
		if (null != authenticatedUser) {
			requestStatistic.setUserId(authenticatedUser.getId());
		}

		if (logRequestBody(requestMethod)) {
			final String requestBodyOriginal = new String(requestWrapper.getContentAsByteArray());
			final String maskedRequestBody = sensitiveDataHelper.redactSensitiveData(requestBodyOriginal);
			requestStatistic.setRequestBody(maskedRequestBody);
		}

		log.info(objectWriter.writeValueAsString(requestStatistic));
	}

	private String getRequestUrl(final HttpServletRequest httpRequest) {
		final StringBuffer requestUrl = httpRequest.getRequestURL();
		final String queryString = httpRequest.getQueryString();
		if (null != queryString) {
			requestUrl.append("?").append(queryString);
		}
		return requestUrl.toString();
	}

	private boolean logRequestBody(final String requestMethod) {
		if (HTTP_UPDATE_METHODS.contains(requestMethod)) {
			return true;
		}
		return false;
	}

}
