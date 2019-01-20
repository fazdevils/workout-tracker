package com.blackwaterpragmatic.joggingtracker.provider;

import static com.blackwaterpragmatic.joggingtracker.constant.RequestScopeAttribute.AUTHENTICATED_USER;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.blackwaterpragmatic.joggingtracker.bean.ResponseMessage;
import com.blackwaterpragmatic.joggingtracker.bean.User;
import com.blackwaterpragmatic.joggingtracker.constant.Role;
import com.blackwaterpragmatic.joggingtracker.constant.RoleName;
import com.blackwaterpragmatic.joggingtracker.helper.ResponseHelper;
import com.blackwaterpragmatic.joggingtracker.service.TokenService;
import com.blackwaterpragmatic.joggingtracker.test.MockHelper;

import org.jboss.resteasy.core.ResourceMethodInvoker;
import org.jboss.resteasy.core.interception.PostMatchContainerRequestContext;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.annotation.security.DenyAll;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;

import java.io.IOException;
import java.lang.reflect.Method;

import io.jsonwebtoken.JwtException;

@RunWith(MockitoJUnitRunner.class)
public class AuthorizationProviderTest {

	@Mock
	private PostMatchContainerRequestContext requestContext;

	@Mock
	private ResourceMethodInvoker methodInvoker;

	@Mock
	private TokenService tokenService;

	@InjectMocks
	private AuthorizationProvider authorizationProvider;

	@Before
	public void before() {
		// using ResponseHelper directly instead of mocking to verify actual response
		authorizationProvider = new AuthorizationProvider(tokenService, new ResponseHelper());
	}

	@Test
	public void should_permit_all() throws IOException, NoSuchMethodException, SecurityException {
		final Method method = getClass().getDeclaredMethod("permitAll");
		when(requestContext.getResourceMethod()).thenReturn(methodInvoker);
		when(methodInvoker.getMethod()).thenReturn(method);

		authorizationProvider.filter(requestContext);

		verify(requestContext).getResourceMethod();
		verify(methodInvoker).getMethod();
		verifyNoMoreInteractions(MockHelper.allDeclaredMocks(this));
	}

	@Test
	public void should_deny_all() throws IOException, NoSuchMethodException, SecurityException {
		final Method method = getClass().getDeclaredMethod("denyAll");
		when(requestContext.getResourceMethod()).thenReturn(methodInvoker);
		when(methodInvoker.getMethod()).thenReturn(method);

		authorizationProvider.filter(requestContext);

		final ArgumentCaptor<Response> responseArgumentCaptor = ArgumentCaptor.forClass(Response.class);
		verify(requestContext).getResourceMethod();
		verify(methodInvoker).getMethod();
		verify(requestContext).abortWith(responseArgumentCaptor.capture());
		verifyNoMoreInteractions(MockHelper.allDeclaredMocks(this));

		final Response response = responseArgumentCaptor.getValue();
		assertEquals(Response.Status.FORBIDDEN.getStatusCode(), response.getStatus());
	}

	@Test
	public void should_permit_declared_role() throws IOException, NoSuchMethodException, SecurityException {
		final Method method = getClass().getDeclaredMethod("permitAdminRole");
		final String authenticationToken = "authenticationToken";
		final String userAgent = "userAgent";
		final User User = new User() {
			{
				setBitwiseRole(Role.ADMIN.getBitwisePermission());
			}
		};
		final HttpServletRequest servletRequest = null; // I couldn't figure out how to inject this into the filter for the tests... so its null

		when(requestContext.getResourceMethod()).thenReturn(methodInvoker);
		when(methodInvoker.getMethod()).thenReturn(method);
		when(requestContext.getHeaderString(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer " + authenticationToken);
		when(requestContext.getHeaderString(HttpHeaders.USER_AGENT)).thenReturn(userAgent);
		when(tokenService.parseAuthenticationToken(authenticationToken, userAgent, servletRequest)).thenReturn(User);

		authorizationProvider.filter(requestContext);

		verify(requestContext).getResourceMethod();
		verify(methodInvoker).getMethod();
		verify(requestContext).getHeaderString(HttpHeaders.AUTHORIZATION);
		verify(requestContext).getHeaderString(HttpHeaders.USER_AGENT);
		verify(tokenService).parseAuthenticationToken(authenticationToken, userAgent, servletRequest);
		verify(requestContext).setProperty(AUTHENTICATED_USER, User);
		verifyNoMoreInteractions(MockHelper.allDeclaredMocks(this));
	}

	@Test
	public void should_error_when_no_security_annotation() throws IOException, NoSuchMethodException, SecurityException {
		final Method method = getClass().getDeclaredMethod("noAnnotation");
		when(requestContext.getResourceMethod()).thenReturn(methodInvoker);
		when(methodInvoker.getMethod()).thenReturn(method);

		authorizationProvider.filter(requestContext);

		final ArgumentCaptor<Response> responseArgumentCaptor = ArgumentCaptor.forClass(Response.class);
		verify(requestContext).getResourceMethod();
		verify(methodInvoker).getMethod();
		verify(requestContext).abortWith(responseArgumentCaptor.capture());
		verifyNoMoreInteractions(MockHelper.allDeclaredMocks(this));

		final Response response = responseArgumentCaptor.getValue();
		assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
		assertEquals("Configuration error.  Missing security annotations on resource.",
				((ResponseMessage) response.getEntity()).getMessage());
	}

	@Test
	public void should_reject_missing_authentication_token() throws IOException, NoSuchMethodException, SecurityException {
		final Method method = getClass().getDeclaredMethod("permitAdminRole");

		when(requestContext.getResourceMethod()).thenReturn(methodInvoker);
		when(methodInvoker.getMethod()).thenReturn(method);

		authorizationProvider.filter(requestContext);

		final ArgumentCaptor<Response> responseArgumentCaptor = ArgumentCaptor.forClass(Response.class);
		verify(requestContext).getResourceMethod();
		verify(methodInvoker).getMethod();
		verify(requestContext).getHeaderString(HttpHeaders.AUTHORIZATION);
		verify(requestContext).abortWith(responseArgumentCaptor.capture());
		verifyNoMoreInteractions(MockHelper.allDeclaredMocks(this));

		final Response response = responseArgumentCaptor.getValue();
		assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
		assertEquals(String.format("Missing '%s' header", HttpHeaders.AUTHORIZATION),
				((ResponseMessage) response.getEntity()).getMessage());
	}

	@Test
	public void should_reject_missing_user_agent() throws IOException, NoSuchMethodException, SecurityException {
		final Method method = getClass().getDeclaredMethod("permitAdminRole");
		final String authenticationToken = "authenticationToken";

		when(requestContext.getResourceMethod()).thenReturn(methodInvoker);
		when(methodInvoker.getMethod()).thenReturn(method);
		when(requestContext.getHeaderString(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer " + authenticationToken);

		authorizationProvider.filter(requestContext);

		final ArgumentCaptor<Response> responseArgumentCaptor = ArgumentCaptor.forClass(Response.class);
		verify(requestContext).getResourceMethod();
		verify(methodInvoker).getMethod();
		verify(requestContext).getHeaderString(HttpHeaders.AUTHORIZATION);
		verify(requestContext).getHeaderString(HttpHeaders.USER_AGENT);
		verify(requestContext).abortWith(responseArgumentCaptor.capture());
		verifyNoMoreInteractions(MockHelper.allDeclaredMocks(this));

		final Response response = responseArgumentCaptor.getValue();
		assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
		assertEquals(String.format("Missing '%s' header", HttpHeaders.USER_AGENT),
				((ResponseMessage) response.getEntity()).getMessage());
	}

	@Test
	public void should_reject_missing_user() throws IOException, NoSuchMethodException, SecurityException {
		final Method method = getClass().getDeclaredMethod("permitAdminRole");
		final String authenticationToken = "authenticationToken";
		final String userAgent = "userAgent";
		final HttpServletRequest servletRequest = null; // I couldn't figure out how to inject this into the filter for the tests... so its null

		when(requestContext.getResourceMethod()).thenReturn(methodInvoker);
		when(methodInvoker.getMethod()).thenReturn(method);
		when(requestContext.getHeaderString(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer " + authenticationToken);
		when(requestContext.getHeaderString(HttpHeaders.USER_AGENT)).thenReturn(userAgent);

		authorizationProvider.filter(requestContext);

		final ArgumentCaptor<Response> responseArgumentCaptor = ArgumentCaptor.forClass(Response.class);
		verify(requestContext).getResourceMethod();
		verify(methodInvoker).getMethod();
		verify(requestContext).getHeaderString(HttpHeaders.AUTHORIZATION);
		verify(requestContext).getHeaderString(HttpHeaders.USER_AGENT);
		verify(requestContext).abortWith(responseArgumentCaptor.capture());
		verify(tokenService).parseAuthenticationToken(authenticationToken, userAgent, servletRequest);
		verifyNoMoreInteractions(MockHelper.allDeclaredMocks(this));

		final Response response = responseArgumentCaptor.getValue();
		assertEquals(Response.Status.UNAUTHORIZED.getStatusCode(), response.getStatus());
		assertEquals("Invalid token",
				((ResponseMessage) response.getEntity()).getMessage());
	}

	@Test
	public void should_reject_method_with_too_many_roles() throws IOException, NoSuchMethodException, SecurityException {
		final Method method = getClass().getDeclaredMethod("tooManyRoles");
		final String authenticationToken = "authenticationToken";
		final String userAgent = "userAgent";
		final User User = new User() {
			{
				setBitwiseRole(Role.ADMIN.getBitwisePermission());
			}
		};
		final HttpServletRequest servletRequest = null; // I couldn't figure out how to inject this into the filter for the tests... so its null

		when(requestContext.getResourceMethod()).thenReturn(methodInvoker);
		when(methodInvoker.getMethod()).thenReturn(method);
		when(requestContext.getHeaderString(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer " + authenticationToken);
		when(requestContext.getHeaderString(HttpHeaders.USER_AGENT)).thenReturn(userAgent);
		when(tokenService.parseAuthenticationToken(authenticationToken, userAgent, servletRequest)).thenReturn(User);

		authorizationProvider.filter(requestContext);

		final ArgumentCaptor<Response> responseArgumentCaptor = ArgumentCaptor.forClass(Response.class);
		verify(requestContext).getResourceMethod();
		verify(methodInvoker).getMethod();
		verify(requestContext).getHeaderString(HttpHeaders.AUTHORIZATION);
		verify(requestContext).getHeaderString(HttpHeaders.USER_AGENT);
		verify(tokenService).parseAuthenticationToken(authenticationToken, userAgent, servletRequest);
		verify(requestContext).abortWith(responseArgumentCaptor.capture());
		verifyNoMoreInteractions(MockHelper.allDeclaredMocks(this));

		final Response response = responseArgumentCaptor.getValue();
		assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
		assertEquals("Configuration error.  @RolesAllowed should have exactly 1 role configured.",
				((ResponseMessage) response.getEntity()).getMessage());
	}

	@Test
	public void should_reject_role_with_different_privileges() throws IOException, NoSuchMethodException, SecurityException {
		final Method method = getClass().getDeclaredMethod("permitAdminRole");
		final String authenticationToken = "authenticationToken";
		final String userAgent = "userAgent";
		final User User = new User() {
			{
				setBitwiseRole(Role.USER.getBitwisePermission());
			}
		};
		final HttpServletRequest servletRequest = null; // I couldn't figure out how to inject this into the filter for the tests... so its null

		when(requestContext.getResourceMethod()).thenReturn(methodInvoker);
		when(methodInvoker.getMethod()).thenReturn(method);
		when(requestContext.getHeaderString(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer " + authenticationToken);
		when(requestContext.getHeaderString(HttpHeaders.USER_AGENT)).thenReturn(userAgent);
		when(tokenService.parseAuthenticationToken(authenticationToken, userAgent, servletRequest)).thenReturn(User);

		authorizationProvider.filter(requestContext);

		final ArgumentCaptor<Response> responseArgumentCaptor = ArgumentCaptor.forClass(Response.class);
		verify(requestContext).getResourceMethod();
		verify(methodInvoker).getMethod();
		verify(requestContext).getHeaderString(HttpHeaders.AUTHORIZATION);
		verify(requestContext).getHeaderString(HttpHeaders.USER_AGENT);
		verify(tokenService).parseAuthenticationToken(authenticationToken, userAgent, servletRequest);
		verify(requestContext).abortWith(responseArgumentCaptor.capture());
		verifyNoMoreInteractions(MockHelper.allDeclaredMocks(this));

		final Response response = responseArgumentCaptor.getValue();
		assertEquals(Response.Status.FORBIDDEN.getStatusCode(), response.getStatus());
		assertEquals("User does not have permissions to access this resource",
				((ResponseMessage) response.getEntity()).getMessage());
	}

	@Test
	public void should_reject_with_invalid_authentication_token() throws IOException, NoSuchMethodException, SecurityException {
		final Method method = getClass().getDeclaredMethod("permitAdminRole");
		final String authenticationToken = "authenticationToken";
		final String userAgent = "userAgent";
		final HttpServletRequest servletRequest = null; // I couldn't figure out how to inject this into the filter for the tests... so its null

		when(requestContext.getResourceMethod()).thenReturn(methodInvoker);
		when(methodInvoker.getMethod()).thenReturn(method);
		when(requestContext.getHeaderString(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer " + authenticationToken);
		when(requestContext.getHeaderString(HttpHeaders.USER_AGENT)).thenReturn(userAgent);
		when(tokenService.parseAuthenticationToken(authenticationToken, userAgent, servletRequest))
				.thenThrow(new JwtException("Mock Exception"));

		authorizationProvider.filter(requestContext);

		final ArgumentCaptor<Response> responseArgumentCaptor = ArgumentCaptor.forClass(Response.class);
		verify(requestContext).getResourceMethod();
		verify(methodInvoker).getMethod();
		verify(requestContext).getHeaderString(HttpHeaders.AUTHORIZATION);
		verify(requestContext).getHeaderString(HttpHeaders.USER_AGENT);
		verify(tokenService).parseAuthenticationToken(authenticationToken, userAgent, servletRequest);
		verify(requestContext).abortWith(responseArgumentCaptor.capture());
		verifyNoMoreInteractions(MockHelper.allDeclaredMocks(this));

		final Response response = responseArgumentCaptor.getValue();
		assertEquals(Response.Status.UNAUTHORIZED.getStatusCode(), response.getStatus());
		assertEquals("Invalid token",
				((ResponseMessage) response.getEntity()).getMessage());
	}

	@Test
	public void should_reject_missing_authentication_bearer_token() throws IOException, NoSuchMethodException, SecurityException {
		final Method method = getClass().getDeclaredMethod("permitAdminRole");

		when(requestContext.getResourceMethod()).thenReturn(methodInvoker);
		when(methodInvoker.getMethod()).thenReturn(method);
		when(requestContext.getHeaderString(HttpHeaders.AUTHORIZATION)).thenReturn("authenticationToken");

		authorizationProvider.filter(requestContext);

		final ArgumentCaptor<Response> responseArgumentCaptor = ArgumentCaptor.forClass(Response.class);
		verify(requestContext).getResourceMethod();
		verify(methodInvoker).getMethod();
		verify(requestContext).getHeaderString(HttpHeaders.AUTHORIZATION);
		verify(requestContext).abortWith(responseArgumentCaptor.capture());
		verifyNoMoreInteractions(MockHelper.allDeclaredMocks(this));

		final Response response = responseArgumentCaptor.getValue();
		assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
		assertEquals(String.format("Missing '%s' header", HttpHeaders.AUTHORIZATION),
				((ResponseMessage) response.getEntity()).getMessage());
	}

	@PermitAll
	private void permitAll() {
	}

	@DenyAll
	private void denyAll() {
	}

	@RolesAllowed(RoleName.ADMIN)
	private void permitAdminRole() {
	}

	@RolesAllowed({RoleName.ADMIN, RoleName.USER})
	private void tooManyRoles() {
	}

	@SuppressWarnings("unused")
	private void noAnnotation() {
	}
}
