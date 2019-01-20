package com.blackwaterpragmatic.joggingtracker.provider;

import static com.blackwaterpragmatic.joggingtracker.constant.RequestScopeAttribute.AUTHENTICATED_USER;
import static org.apache.logging.log4j.LogManager.getLogger;

import com.blackwaterpragmatic.joggingtracker.bean.User;
import com.blackwaterpragmatic.joggingtracker.constant.Role;
import com.blackwaterpragmatic.joggingtracker.helper.ResponseHelper;
import com.blackwaterpragmatic.joggingtracker.service.TokenService;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.jboss.resteasy.core.interception.PostMatchContainerRequestContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.security.DenyAll;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.jsonwebtoken.JwtException;

@Component
@Provider
public class AuthorizationProvider implements ContainerRequestFilter {

	private static final Logger log = getLogger(AuthorizationProvider.class);

	@Context
	private HttpServletRequest servletRequest;

	private final TokenService tokenService;
	private final ResponseHelper responseHelper;
	private final Pattern tokenPattern;

	@Autowired
	public AuthorizationProvider(
			final TokenService tokenService,
			final ResponseHelper responseHelper) {
		this.tokenService = tokenService;
		this.responseHelper = responseHelper;
		tokenPattern = Pattern.compile("^Bearer (.+)");
	}

	@Override
	public void filter(final ContainerRequestContext requestContext) throws IOException {
		final PostMatchContainerRequestContext resteasyRequestContext = (PostMatchContainerRequestContext) requestContext;
		final Method method = resteasyRequestContext.getResourceMethod().getMethod();

		if (method.isAnnotationPresent(PermitAll.class)) {
			return;
		} else if (method.isAnnotationPresent(DenyAll.class)) {
			requestContext.abortWith(Response.status(Response.Status.FORBIDDEN).build());
		} else if (method.isAnnotationPresent(RolesAllowed.class)) {
			authorizeRoleAccess(requestContext, method);
		} else {
			requestContext.abortWith(
					responseHelper.build(
							Response.Status.INTERNAL_SERVER_ERROR,
							"Configuration error.  Missing security annotations on resource."));
		}
	}

	private void authorizeRoleAccess(
			final ContainerRequestContext requestContext,
			final Method method) {

		final String authenticationToken = getAuthenticationToken(requestContext);
		if (StringUtils.isEmpty(authenticationToken)) {
			requestContext.abortWith(
					responseHelper.build(
							Response.Status.BAD_REQUEST,
							String.format("Missing '%s' header", HttpHeaders.AUTHORIZATION)));
			return;
		}

		final String userAgent = getUserAgent(requestContext);
		if (StringUtils.isEmpty(userAgent)) {
			requestContext.abortWith(
					responseHelper.build(
							Response.Status.BAD_REQUEST,
							String.format("Missing '%s' header", HttpHeaders.USER_AGENT)));
			return;
		}

		final User authenticatedUser = getAuthenticatedUser(authenticationToken, userAgent);
		if (null == authenticatedUser) {
			requestContext.abortWith(
					responseHelper.build(
							Response.Status.UNAUTHORIZED,
							"Invalid token"));
			return;
		}

		final String[] methodRoleNames = method.getAnnotation(RolesAllowed.class).value();
		if (wrongNumberOfRolesDefinedOnMethod(methodRoleNames)) {
			requestContext.abortWith(
					responseHelper.build(
							Response.Status.INTERNAL_SERVER_ERROR,
							"Configuration error.  @RolesAllowed should have exactly 1 role configured."));
			return;
		}

		final Integer userBitwiseRole = authenticatedUser.getBitwiseRole();
		final Integer methodBitwisePermission = Role.valueOf(methodRoleNames[0]).getBitwisePermission();

		if (userCanAccessResource(userBitwiseRole, methodBitwisePermission)) {
			requestContext.setProperty(AUTHENTICATED_USER, authenticatedUser);
			return;
		} else {
			requestContext.abortWith(
					responseHelper.build(
							Response.Status.FORBIDDEN,
							"User does not have permissions to access this resource"));
			return;
		}
	}

	private User getAuthenticatedUser(
			final String authenticationToken,
			final String userAgent) {
		try {
			return tokenService.parseAuthenticationToken(authenticationToken, userAgent, servletRequest);
		} catch (final JwtException jwtException) {
			log.info(jwtException);
			return null;
		}
	}

	private String getAuthenticationToken(final ContainerRequestContext requestContext) {
		final String authenticationString = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);
		if (!StringUtils.isEmpty(authenticationString)) {
			final Matcher tokenMatcher = tokenPattern.matcher(authenticationString);
			if (tokenMatcher.find()) {
				return tokenMatcher.group(1);
			}
		}
		return null;
	}

	private String getUserAgent(final ContainerRequestContext requestContext) {
		return requestContext.getHeaderString(HttpHeaders.USER_AGENT);
	}

	private boolean wrongNumberOfRolesDefinedOnMethod(final String[] methodRoleNames) {
		return methodRoleNames.length != 1;
	}

	private boolean userCanAccessResource(final Integer userBitwiseRole, final Integer methodBitwisePermission) {
		return (userBitwiseRole & methodBitwisePermission) != 0;
	}

}
