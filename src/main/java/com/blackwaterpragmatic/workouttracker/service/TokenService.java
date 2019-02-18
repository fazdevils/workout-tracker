package com.blackwaterpragmatic.workouttracker.service;

import com.blackwaterpragmatic.workouttracker.bean.User;
import com.blackwaterpragmatic.workouttracker.bean.internal.ApplicationEnvironment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;

import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import java.util.Date;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MissingClaimException;
import io.jsonwebtoken.SignatureAlgorithm;

@Service
public class TokenService {

	private static final SignatureAlgorithm SIGNATURE_ALGORITHM = SignatureAlgorithm.HS512;
	private static final String AGENT = "agent";
	private static final String IP = "ip";
	private static final String ISSUER = "blackprag_wrkouttrkr"; // TODO make this a config param?
	private static final String ROLE = "role";
	private static final String USER_ID = "userId";
	private static final String ENV = "env";

	private final SecretKey jwtSignatureKey;
	private final Integer jwtExpirationHours;
	private final Integer jwtExpirationMinutes;
	private final String env;

	@Autowired
	public TokenService(final ApplicationEnvironment applicationEnvironment) {

		final byte[] jwsSignature = applicationEnvironment.getJwtSignature().getBytes(StandardCharsets.UTF_8);
		jwtSignatureKey = new SecretKeySpec(jwsSignature, SIGNATURE_ALGORITHM.getJcaName());
		jwtExpirationHours = applicationEnvironment.getJwtExpirationHours();
		jwtExpirationMinutes = applicationEnvironment.getJwtExpirationMinutes();
		env = applicationEnvironment.getEnv();
	}

	public User parseAuthenticationToken(
			final String authenticationToken,
			final String userAgent,
			final HttpServletRequest request) {

		final Jws<Claims> parsedToken = Jwts.parser()
				.requireIssuer(ISSUER)
				.require(IP, getIpAddress(request))
				.require(AGENT, userAgent.hashCode())
				.require(ENV, env.hashCode())
				.setSigningKey(jwtSignatureKey)
				.parseClaimsJws(authenticationToken);

		final User authenticatedUser = new User();
		authenticatedUser.setId(getClaim(parsedToken, USER_ID, Long.class));
		authenticatedUser.setBitwiseRole(getClaim(parsedToken, ROLE, Integer.class));

		return authenticatedUser;
	}

	public String buildAuthenticationToken(
			final User authenticatedUser,
			final String userAgent,
			final HttpServletRequest request) {

		return Jwts.builder()
				.setIssuer(ISSUER)
				.setExpiration(getExpiration())
				.claim(IP, getIpAddress(request))
				.claim(AGENT, userAgent.hashCode())
				.claim(ENV, env.hashCode())
				.claim(USER_ID, authenticatedUser.getId())
				.claim(ROLE, authenticatedUser.getBitwiseRole())
				.signWith(SIGNATURE_ALGORITHM, jwtSignatureKey)
				.compact();
	}

	private <T> T getClaim(final Jws<Claims> parsedToken, final String claimName, final Class<T> type) {
		final Claims claims = parsedToken.getBody();
		final T claim = claims.get(claimName, type);
		if (null == claim) {
			throw new MissingClaimException(parsedToken.getHeader(), claims, "The value cannot be null for claim name: " + claimName);
		}
		return claim;
	}

	private Date getExpiration() {
		final Calendar now = Calendar.getInstance();
		now.add(Calendar.HOUR, jwtExpirationHours);
		now.add(Calendar.MINUTE, jwtExpirationMinutes);
		return now.getTime();
	}

	private String getIpAddress(final HttpServletRequest request) {
		return request.getRemoteAddr();
	}
}
