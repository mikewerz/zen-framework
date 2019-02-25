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

package com.mikewerzen.zen.zenframework.security.provider.zen.jwt.token;

import com.mikewerzen.zen.zenframework.exception.system.InternalException;
import com.mikewerzen.zen.zenframework.logging.context.LoggingContextHolder;
import com.mikewerzen.zen.zenframework.security.context.SecurityContextHolder;
import com.mikewerzen.zen.zenframework.security.provider.zen.jwt.service.TokenService;
import com.mikewerzen.zen.zenframework.security.token.SecurityToken;


public class JWTToken implements SecurityToken
{
	private TokenService tokenService;

	private String appName;
	private String userId;
	private String username;
	private String[] roles;
	private String[] events;

	private String tokenId;
	private String rawTokenText;

	public JWTToken(TokenService tokenService, String appName, String userId, String username,
			String[] roles, String[] events, String tokenId, String rawTokenText)
	{
		this.tokenService = tokenService;
		this.appName = appName;
		this.userId = userId;
		this.username = username;
		this.roles = roles;
		this.events = events;
		this.tokenId = tokenId;
		this.rawTokenText = rawTokenText;
	}

	public static JWTToken createAuthorizationToken(TokenService tokenService, String appName, String userId,
			String username, String[] roles,
			String[] events, String tokenId, String rawTokenText)
	{
		return new JWTToken(tokenService, appName, userId, username, roles, events, tokenId, rawTokenText);
	}

	public static JWTToken createToken(TokenService tokenService, String appName, String
			userId,
			String username,
			String[] roles, String[] events, String tokenId, String rawTokenText)
	{
			return createAuthorizationToken(tokenService,
					appName,
					userId,
					username,
					roles,
					events,
					tokenId,
					rawTokenText);
	}

	@Override public String getTokenId()
	{
		return tokenId;
	}

	@Override public String getRawToken()
	{
		return rawTokenText;
	}

	@Override public String getAppName()
	{
		return appName;
	}

	@Override public String getUserId()
	{
		return userId;
	}

	@Override public String getUsername()
	{
		return username;
	}

	@Override public String[] getRoles()
	{
		return roles;
	}

	@Override public String[] getEvents()
	{
		return events;
	}
}
