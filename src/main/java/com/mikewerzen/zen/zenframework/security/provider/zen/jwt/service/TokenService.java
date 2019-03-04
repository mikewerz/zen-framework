/* Copyright  2019, Michael Werzen
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Portions of this software are Copyright 2018, TessaTech LLC.
 *
 * Such portions are licensed under the MIT License (the "License"); you may not use this file
 *  except in compliance with the License.
 * You may obtain a copy of the License at
 *       https://opensource.org/licenses/MIT
 */

package com.mikewerzen.zen.zenframework.security.provider.zen.jwt.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.mikewerzen.zen.zenframework.exception.logic.InvalidAuthenticationException;
import com.mikewerzen.zen.zenframework.exception.system.InternalException;
import com.mikewerzen.zen.zenframework.security.provider.zen.jwt.client.ZenIAMServiceClient;
import com.mikewerzen.zen.zenframework.security.provider.zen.jwt.client.container.Secret;
import com.mikewerzen.zen.zenframework.security.provider.zen.jwt.token.JWTToken;
import com.mikewerzen.zen.zenframework.validation.InternalValidationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.time.Instant;
import java.time.ZonedDateTime;

@Component
public class TokenService
{
	@Autowired ZenIAMServiceClient iamServiceClient;

	@Value("${security.jwt.issuer}")
	private String jwtIssuer = null;

	@Value("${security.jwt.leeway:0}")
	private long jwtLeeway = 0;

	private Algorithm currentAlgorithm;
	private JWTVerifier currentVerifier;

	private Algorithm previousAlgorithm;
	private JWTVerifier previousVerifier;

	public String createAuthorizationToken(String userId, String appName, String userName, String[] userRoles,
			String[] userEvents, ZonedDateTime expiresAt)
	{
		return JWT.create()
				.withIssuer(jwtIssuer)
				.withIssuedAt(java.util.Date.from(Instant.now().minusSeconds(15)))
				.withExpiresAt(java.util.Date.from(expiresAt.toInstant()))
				.withClaim("userId", userId)
				.withClaim("appName", appName)
				.withClaim("username", userName)
				.withArrayClaim("userRoles", userRoles)
				.withArrayClaim("userEvents", userEvents)
				.sign(currentAlgorithm);
	}

	public JWTToken decodeToken(String token)
	{
		verifyToken(token);

		DecodedJWT decodedToken = JWT.decode(token);
		String jwtId = decodedToken.getId();

		String typeString = decodedToken.getClaim("type").asString();
		String userId = decodedToken.getClaim("userId").asString();
		String appName = decodedToken.getClaim("appName").asString();
		String username = decodedToken.getClaim("username").asString();
		String[] userRoles = decodedToken.getClaim("userRoles").asArray(String.class);
		String[] userEvents = decodedToken.getClaim("userEvents").asArray(String.class);

			return JWTToken.createAuthorizationToken(this, appName, userId, username, userRoles, userEvents, jwtId, token);

	}

	public void verifyToken(String token)
	{
		try
		{
			currentVerifier.verify(token);
		}
		catch (JWTVerificationException | NullPointerException currentVerifierException)
		{
			//Perhaps the token is old, try again with previous verifier.
			try
			{
				previousVerifier.verify(token);
			}
			catch (JWTVerificationException | NullPointerException exception)
			{
				throw new InvalidAuthenticationException("Could not verify JWT token", currentVerifierException);
			}
		}
	}

	public void updateSecret(Secret secret)
	{
		if (secret.previousSecret != null)
		{
			previousAlgorithm = createAlgorithm(secret.previousSecret);
			previousVerifier = createVerifier(previousAlgorithm);
		}

		currentAlgorithm = createAlgorithm(secret.currentSecret);
		currentVerifier = createVerifier(currentAlgorithm);
	}

	private Algorithm createAlgorithm(String secret)
	{
		InternalValidationUtils.getInstance().isNotTrimmedEmpty("secret", secret);

		try
		{
			return Algorithm.HMAC256(secret);
		}
		catch (UnsupportedEncodingException e)
		{
			throw new InternalException("Could not load JWT HMAC-SHA256 Algorithm", e);
		}

	}

	private JWTVerifier createVerifier(Algorithm algorithm)
	{
		InternalValidationUtils.getInstance().isNotTrimmedEmpty("jwtIssuer", jwtIssuer);

		return JWT.require(algorithm).withIssuer(jwtIssuer).acceptLeeway(jwtLeeway).build();
	}

}
