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

package com.mikewerzen.zen.zenframework.security.provider.zen.jwt;

import com.mikewerzen.zen.zenframework.exception.logic.InvalidAuthenticationException;
import com.mikewerzen.zen.zenframework.rest.request.ZenHttpHeaders;
import com.mikewerzen.zen.zenframework.security.context.SecurityContext;
import com.mikewerzen.zen.zenframework.security.context.SecurityContextHolder;
import com.mikewerzen.zen.zenframework.security.provider.SecurityProvider;
import com.mikewerzen.zen.zenframework.security.provider.zen.jwt.service.TokenService;
import com.mikewerzen.zen.zenframework.security.provider.zen.jwt.token.JWTToken;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Component;


import java.util.Optional;

@Component
public class ZenJWTSecurityProvider implements SecurityProvider
{
	private static final Logger logger = LogManager.getLogger(ZenJWTSecurityProvider.class);

	private static final String authenticationScheme = "Bearer";

	@Autowired
	private TokenService tokenService;

	@Override
	public void loadAndVerifySecurityDetails(RequestEntity requestEntity)
			throws InvalidAuthenticationException
	{
		Optional<String> rawTokenOptional = retrieveAndValidateToken(requestEntity);

		if (rawTokenOptional.isPresent())
		{
			JWTToken token = tokenService.decodeToken(rawTokenOptional.get());

			SecurityContext context = new SecurityContext(token);
			SecurityContextHolder.setContext(context);
		}
	}

	private Optional<String> retrieveAndValidateToken(RequestEntity requestEntity)
	{
		if (requestEntity == null || requestEntity.getHeaders() == null)
		{
			return Optional.empty();
		}

		ZenHttpHeaders headers = new ZenHttpHeaders(requestEntity.getHeaders());

		String auth = headers.getAuthorization();
		if (auth == null)
		{
			return Optional.empty();
		}

		String[] authSplit = auth.split(" ");

		if (authSplit.length != 2)
		{
			return Optional.empty();
		}

		String scheme = authSplit[0];
		String token = authSplit[1];

		if (authenticationScheme.equalsIgnoreCase(scheme))
		{
			if (StringUtils.isNotBlank(token))
			{
				return Optional.of(token);
			}
		}

		return Optional.empty();
	}
}
