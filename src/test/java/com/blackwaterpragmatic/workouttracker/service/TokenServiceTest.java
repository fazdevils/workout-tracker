package com.blackwaterpragmatic.workouttracker.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.blackwaterpragmatic.workouttracker.bean.User;
import com.blackwaterpragmatic.workouttracker.bean.internal.ApplicationEnvironment;
import com.blackwaterpragmatic.workouttracker.constant.Role;
import com.blackwaterpragmatic.workouttracker.service.TokenService;
import com.blackwaterpragmatic.workouttracker.test.MockHelper;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.servlet.http.HttpServletRequest;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.IncorrectClaimException;
import io.jsonwebtoken.MissingClaimException;
import io.jsonwebtoken.SignatureException;

@RunWith(MockitoJUnitRunner.class)
public class TokenServiceTest {

	private final String signature = "signatureKey";
	private final int expirationHours = 0;
	private final int expirationMinutes = 1;
	private final String env = "ut";

	@Mock
	private HttpServletRequest request;

	@Test
	public void should_validate_authentication_token() {
		final String userAgent = "userAgent";
		final String ipAddress = "1.2.3.4";

		final Long userId = 1L;

		final User User = new User() {
			{
				setId(userId);
				setBitwiseRole(Role.ADMIN.getBitwisePermission());
			}
		};

		final TokenService tokenService = buildTokenService(signature, expirationHours, expirationMinutes, env);

		when(request.getRemoteAddr()).thenReturn(ipAddress);

		final String authenticationToken = tokenService.buildAuthenticationToken(User, userAgent, request);

		verify(request).getRemoteAddr();
		verifyNoMoreInteractions(MockHelper.allDeclaredMocks(this));
		reset(MockHelper.allDeclaredMocks(this));

		assertNotNull(authenticationToken);

		// Parse the encrypted authentication token to get the result back
		when(request.getRemoteAddr()).thenReturn(ipAddress);
		final User authorizedUser = tokenService.parseAuthenticationToken(authenticationToken, userAgent, request);

		verify(request).getRemoteAddr();
		verifyNoMoreInteractions(MockHelper.allDeclaredMocks(this));
		reset(MockHelper.allDeclaredMocks(this));

		assertEquals(userId, authorizedUser.getId());
		assertEquals(Role.ADMIN.getBitwisePermission(), authorizedUser.getBitwiseRole().intValue());

		// Parse the encrypted authentication again with a different instance
		final TokenService tokenService2 = buildTokenService(signature, expirationHours, expirationMinutes, env);
		when(request.getRemoteAddr()).thenReturn(ipAddress);
		final User authorizedUser2 = tokenService2.parseAuthenticationToken(authenticationToken, userAgent, request);

		verify(request).getRemoteAddr();
		verifyNoMoreInteractions(MockHelper.allDeclaredMocks(this));
		reset(MockHelper.allDeclaredMocks(this));

		assertEquals(userId, authorizedUser2.getId());
		assertEquals(Role.ADMIN.getBitwisePermission(), authorizedUser2.getBitwiseRole().intValue());
	}

	@Test
	public void should_fail_with_invalid_signature_key() {
		final String userAgent = "userAgent";
		final String ipAddress = "1.2.3.4";

		final Long userId = 1L;

		final User User = new User() {
			{
				setId(userId);
				setBitwiseRole(Role.ADMIN.getBitwisePermission());
			}
		};

		final TokenService tokenService = buildTokenService(signature, expirationHours, expirationMinutes, env);

		// Create an authentication token
		when(request.getRemoteAddr()).thenReturn(ipAddress);

		final String authenticationToken = tokenService.buildAuthenticationToken(User, userAgent, request);

		verify(request).getRemoteAddr();
		verifyNoMoreInteractions(MockHelper.allDeclaredMocks(this));
		reset(MockHelper.allDeclaredMocks(this));

		assertNotNull(authenticationToken);

		// Parse the encrypted authentication with a different signature
		final TokenService tokenService2 = buildTokenService(signature + "X", expirationHours, expirationMinutes, env);
		when(request.getRemoteAddr()).thenReturn(ipAddress);

		try {
			tokenService2.parseAuthenticationToken(authenticationToken, userAgent, request);
			fail();
		} catch (final SignatureException e) {
			verify(request).getRemoteAddr();
			verifyNoMoreInteractions(MockHelper.allDeclaredMocks(this));
			reset(MockHelper.allDeclaredMocks(this));

			assertEquals(
					"JWT signature does not match locally computed signature. JWT validity cannot be asserted and should not be trusted.",
					e.getMessage());
		}

		// Parse the encrypted authentication with a different signature
		final TokenService tokenService3 = buildTokenService(signature.substring(signature.length() - 1), expirationHours, expirationMinutes, env);
		when(request.getRemoteAddr()).thenReturn(ipAddress);

		try {
			tokenService3.parseAuthenticationToken(authenticationToken, userAgent, request);
			fail();
		} catch (final SignatureException e) {
			verify(request).getRemoteAddr();
			verifyNoMoreInteractions(MockHelper.allDeclaredMocks(this));
			reset(MockHelper.allDeclaredMocks(this));

			assertEquals(
					"JWT signature does not match locally computed signature. JWT validity cannot be asserted and should not be trusted.",
					e.getMessage());
		}
	}

	@Test
	public void should_fail_parsing_with_invalid_env() {
		final String userAgent = "userAgent";
		final String ipAddress = "1.2.3.4";

		final Long userId = 1L;

		final User User = new User() {
			{
				setId(userId);
				setBitwiseRole(Role.ADMIN.getBitwisePermission());
			}
		};

		final TokenService tokenService = buildTokenService(signature, expirationHours, expirationMinutes, env);

		// Create an authentication token
		when(request.getRemoteAddr()).thenReturn(ipAddress);

		final String authenticationToken = tokenService.buildAuthenticationToken(User, userAgent, request);

		verify(request).getRemoteAddr();
		verifyNoMoreInteractions(MockHelper.allDeclaredMocks(this));
		reset(MockHelper.allDeclaredMocks(this));

		assertNotNull(authenticationToken);

		// Parse the encrypted authentication with a different signature
		final TokenService tokenService2 = buildTokenService(signature, expirationHours, expirationMinutes, "dev");
		when(request.getRemoteAddr()).thenReturn(ipAddress);

		try {
			tokenService2.parseAuthenticationToken(authenticationToken, userAgent, request);
			fail();
		} catch (final IncorrectClaimException e) {
			verify(request).getRemoteAddr();
			verifyNoMoreInteractions(MockHelper.allDeclaredMocks(this));
			reset(MockHelper.allDeclaredMocks(this));

			assertTrue(e.getMessage().startsWith("Expected env claim to be:"));
		}
	}

	@Test
	public void should_fail_parsing_with_expired_token() {
		final String userAgent = "userAgent";
		final String ipAddress = "1.2.3.4";

		final Long userId = 1L;

		final User User = new User() {
			{
				setId(userId);
				setBitwiseRole(Role.ADMIN.getBitwisePermission());
			}
		};

		final TokenService tokenService = buildTokenService(signature, expirationHours, -1, env);

		// Create an authentication token
		when(request.getRemoteAddr()).thenReturn(ipAddress);

		final String authenticationToken = tokenService.buildAuthenticationToken(User, userAgent, request);

		verify(request).getRemoteAddr();
		verifyNoMoreInteractions(MockHelper.allDeclaredMocks(this));
		reset(MockHelper.allDeclaredMocks(this));

		assertNotNull(authenticationToken);

		when(request.getRemoteAddr()).thenReturn(ipAddress);

		try {
			tokenService.parseAuthenticationToken(authenticationToken, userAgent, request);
			fail();
		} catch (final ExpiredJwtException e) {
			verify(request).getRemoteAddr();
			verifyNoMoreInteractions(MockHelper.allDeclaredMocks(this));
			reset(MockHelper.allDeclaredMocks(this));

			assertTrue(e.getMessage().startsWith("JWT expired at"));
		}

	}

	@Test
	public void should_fail_parsing_with_invalid_ip() {
		final String userAgent = "userAgent";
		final String ipAddress = "1.2.3.4";

		final Long userId = 1L;

		final User User = new User() {
			{
				setId(userId);
				setBitwiseRole(Role.ADMIN.getBitwisePermission());
			}
		};

		final TokenService tokenService = buildTokenService(signature, expirationHours, expirationMinutes, env);

		// Create an authentication token
		when(request.getRemoteAddr()).thenReturn(ipAddress);

		final String authenticationToken = tokenService.buildAuthenticationToken(User, userAgent, request);

		verify(request).getRemoteAddr();
		verifyNoMoreInteractions(MockHelper.allDeclaredMocks(this));
		reset(MockHelper.allDeclaredMocks(this));

		assertNotNull(authenticationToken);

		final String ipAddress2 = "4.3.2.1";
		when(request.getRemoteAddr()).thenReturn(ipAddress2);

		try {
			tokenService.parseAuthenticationToken(authenticationToken, userAgent, request);
			fail();
		} catch (final IncorrectClaimException e) {
			verify(request).getRemoteAddr();
			verifyNoMoreInteractions(MockHelper.allDeclaredMocks(this));
			reset(MockHelper.allDeclaredMocks(this));

			assertTrue(e.getMessage().startsWith("Expected ip claim to be:"));
		}

	}

	@Test
	public void should_fail_parsing_with_missing_ip() {
		final String userAgent = "userAgent";
		final String ipAddress = "1.2.3.4";

		final Long userId = 1L;

		final User User = new User() {
			{
				setId(userId);
				setBitwiseRole(Role.ADMIN.getBitwisePermission());
			}
		};

		final TokenService tokenService = buildTokenService(signature, expirationHours, expirationMinutes, env);

		// Create an authentication token
		when(request.getRemoteAddr()).thenReturn(null);

		final String authenticationToken = tokenService.buildAuthenticationToken(User, userAgent, request);

		verify(request).getRemoteAddr();
		verifyNoMoreInteractions(MockHelper.allDeclaredMocks(this));
		reset(MockHelper.allDeclaredMocks(this));

		assertNotNull(authenticationToken);

		when(request.getRemoteAddr()).thenReturn(ipAddress);

		try {
			tokenService.parseAuthenticationToken(authenticationToken, userAgent, request);
			fail();
		} catch (final MissingClaimException e) {
			verify(request).getRemoteAddr();
			verifyNoMoreInteractions(MockHelper.allDeclaredMocks(this));
			reset(MockHelper.allDeclaredMocks(this));

			assertEquals("Expected ip claim to be: 1.2.3.4, but was not present in the JWT claims.", e.getMessage());
		}

	}

	@Test
	public void should_fail_parsing_with_invalid_agent() {
		final String userAgent = "userAgent";
		final String ipAddress = "1.2.3.4";

		final Long userId = 1L;

		final User User = new User() {
			{
				setId(userId);
				setBitwiseRole(Role.ADMIN.getBitwisePermission());
			}
		};

		final TokenService tokenService = buildTokenService(signature, expirationHours, expirationMinutes, env);

		// Create an authentication token
		when(request.getRemoteAddr()).thenReturn(ipAddress);

		final String authenticationToken = tokenService.buildAuthenticationToken(User, userAgent, request);

		verify(request).getRemoteAddr();
		verifyNoMoreInteractions(MockHelper.allDeclaredMocks(this));
		reset(MockHelper.allDeclaredMocks(this));

		assertNotNull(authenticationToken);

		when(request.getRemoteAddr()).thenReturn(ipAddress);

		try {
			tokenService.parseAuthenticationToken(authenticationToken, userAgent + "X", request);
			fail();
		} catch (final IncorrectClaimException e) {
			verify(request).getRemoteAddr();
			verifyNoMoreInteractions(MockHelper.allDeclaredMocks(this));
			reset(MockHelper.allDeclaredMocks(this));

			assertTrue(e.getMessage().startsWith("Expected agent claim to be:"));
		}

	}

	@Test
	public void should_fail_parsing_with_missing_user() {
		final String userAgent = "userAgent";
		final String ipAddress = "1.2.3.4";

		final User User = new User() {
			{
				setBitwiseRole(Role.ADMIN.getBitwisePermission());
			}
		};

		final TokenService tokenService = buildTokenService(signature, expirationHours, expirationMinutes, env);

		// Create an authentication token
		when(request.getRemoteAddr()).thenReturn(ipAddress);

		final String authenticationToken = tokenService.buildAuthenticationToken(User, userAgent, request);

		verify(request).getRemoteAddr();
		verifyNoMoreInteractions(MockHelper.allDeclaredMocks(this));
		reset(MockHelper.allDeclaredMocks(this));

		assertNotNull(authenticationToken);

		when(request.getRemoteAddr()).thenReturn(ipAddress);

		try {
			tokenService.parseAuthenticationToken(authenticationToken, userAgent, request);
			fail();
		} catch (final MissingClaimException e) {
			verify(request).getRemoteAddr();
			verifyNoMoreInteractions(MockHelper.allDeclaredMocks(this));
			reset(MockHelper.allDeclaredMocks(this));

			assertEquals("The value cannot be null for claim name: userId", e.getMessage());
		}

	}

	@Test
	public void should_fail_parsing_with_missing_role() {
		final String userAgent = "userAgent";
		final String ipAddress = "1.2.3.4";

		final Long userId = 1L;

		final User User = new User() {
			{
				setId(userId);
			}
		};

		final TokenService tokenService = buildTokenService(signature, expirationHours, expirationMinutes, env);

		// Create an authentication token
		when(request.getRemoteAddr()).thenReturn(ipAddress);

		final String authenticationToken = tokenService.buildAuthenticationToken(User, userAgent, request);

		verify(request).getRemoteAddr();
		verifyNoMoreInteractions(MockHelper.allDeclaredMocks(this));
		reset(MockHelper.allDeclaredMocks(this));

		assertNotNull(authenticationToken);

		when(request.getRemoteAddr()).thenReturn(ipAddress);

		try {
			tokenService.parseAuthenticationToken(authenticationToken, userAgent, request);
			fail();
		} catch (final MissingClaimException e) {
			verify(request).getRemoteAddr();
			verifyNoMoreInteractions(MockHelper.allDeclaredMocks(this));
			reset(MockHelper.allDeclaredMocks(this));

			assertEquals("The value cannot be null for claim name: role", e.getMessage());
		}

	}

	private TokenService buildTokenService(
			final String signature,
			final int expirationHours,
			final int expirationMinutes,
			final String env) {

		final ApplicationEnvironment applicationEnvironment = new ApplicationEnvironment() {
			{
				setJwtSignature(signature);
				setJwtExpirationHours(expirationHours);
				setJwtExpirationMinutes(expirationMinutes);
				setEnv(env);
			}
		};

		return new TokenService(applicationEnvironment);
	}

}
