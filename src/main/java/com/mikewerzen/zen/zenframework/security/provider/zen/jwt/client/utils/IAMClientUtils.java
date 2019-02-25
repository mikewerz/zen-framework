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

package com.mikewerzen.zen.zenframework.security.provider.zen.jwt.client.utils;

import com.mikewerzen.zen.zenframework.security.provider.zen.jwt.client.ZenIAMServiceClient;
import com.mikewerzen.zen.zenframework.security.provider.zen.jwt.client.container.SecretAuthentication;
import org.apache.commons.codec.digest.HmacAlgorithms;
import org.apache.commons.codec.digest.HmacUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class IAMClientUtils
{
	private static ZenIAMServiceClient zenIamServiceClient;

	@Value("${security.zen.iam.secret.hmac.key}")
	private String hmacKey;

	public SecretAuthentication generateSecretAuthorization(String appName)
	{
		SecretAuthentication request = new SecretAuthentication();
		request.appName = appName;
		request.timestamp = System.currentTimeMillis();
		request.nonce = RandomStringUtils.randomAlphanumeric(24);
		request.signature = generateSignature(request);
		return request;
	}

	public String generateSignature(SecretAuthentication request)
	{
		HmacUtils hmac = new HmacUtils(HmacAlgorithms.HMAC_SHA_512, hmacKey);
		return hmac.hmacHex(request.appName + ":" + request.timestamp + ":" + request.nonce);
	}
}
