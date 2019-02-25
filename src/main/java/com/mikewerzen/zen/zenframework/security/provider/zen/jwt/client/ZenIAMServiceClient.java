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

package com.mikewerzen.zen.zenframework.security.provider.zen.jwt.client;

import com.mikewerzen.zen.zenframework.event.ZenEvent;
import com.mikewerzen.zen.zenframework.rest.client.ZenRestClient;
import com.mikewerzen.zen.zenframework.security.provider.zen.jwt.client.container.Secret;
import com.mikewerzen.zen.zenframework.security.provider.zen.jwt.client.container.Token;
import com.mikewerzen.zen.zenframework.security.provider.zen.jwt.client.utils.IAMClientUtils;
import com.mikewerzen.zen.zenframework.security.utils.SecurityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


import java.util.Optional;

@Component
public class ZenIAMServiceClient
{
	private static final Logger logger = LogManager.getLogger(ZenIAMServiceClient.class);

	@Autowired
	private com.mikewerzen.zen.zenframework.security.provider.zen.jwt.client.utils.IAMClientUtils IAMClientUtils;

	@Autowired
	private SecurityUtils securityUtils;

	@Value("${security.zen.iam.app.name}")
	private String appName;

	@Value("${security.zen.iam.secret.endpoint.url}")
	private String secretEndpointUrl;

	@Value("${security.zen.iam.token.endpoint.url}")
	private String tokenEndpointUrl;

	private ZenRestClient client = new ZenRestClient("IAM Service");

	@ZenEvent(eventName = "retrieveLatestSecret", eventGroup = "Framework")
	public Optional<Secret> retrieveLatestSecret()
	{
		try
		{
			return Optional.of(client
					.post("/secret",
							secretEndpointUrl,
							IAMClientUtils.generateSecretAuthorization(appName),
							Secret.class));
		}
		catch (Exception e)
		{
			logger.error("Error occurred retrieving secret from IAM Service.", e);
		}

		return Optional.empty();
	}

}
