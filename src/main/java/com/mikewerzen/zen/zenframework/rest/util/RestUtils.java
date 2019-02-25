/*
 * Copyright  2019, Michael Werzen
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

package com.mikewerzen.zen.zenframework.rest.util;

import com.google.gson.Gson;
import com.mikewerzen.zen.zenframework.event.context.EventContext;
import com.mikewerzen.zen.zenframework.event.context.EventContextHolder;
import com.mikewerzen.zen.zenframework.rest.request.ZenHttpHeaders;
import com.mikewerzen.zen.zenframework.security.context.SecurityContext;
import com.mikewerzen.zen.zenframework.security.context.SecurityContextHolder;
import com.mikewerzen.zen.zenframework.transaction.context.TransactionContext;
import com.mikewerzen.zen.zenframework.transaction.context.TransactionContextHolder;
import com.mikewerzen.zen.zenframework.util.UniqueIdentifierUtils;
import org.springframework.http.HttpHeaders;


public class RestUtils
{
	private static final Gson gson = new Gson();

	public static ZenHttpHeaders buildZenHttpHeaders()
	{
		ZenHttpHeaders headers = new ZenHttpHeaders();
		EventContextHolder.getContextOptional().ifPresent(context -> populateZenHeaders(headers, context));
		TransactionContextHolder.getContextOptional().ifPresent(context -> populateZenHeaders(headers, context));
		return headers;
	}

	public static HttpHeaders buildHttpHeaders()
	{
		return buildZenHttpHeaders().getHeaders();
	}

	public static ZenHttpHeaders buildZenHttpHeadersWithAuth()
	{
		ZenHttpHeaders headers = buildZenHttpHeaders();
		addClientAuthToHeaders(headers);
		return headers;
	}

	public static HttpHeaders buildHttpHeadersWithAuth()
	{
		return buildZenHttpHeadersWithAuth().getHeaders();
	}

	private static ZenHttpHeaders addClientAuthToHeaders(ZenHttpHeaders headers)
	{
		SecurityContext context = SecurityContextHolder.getContext();

		//TODO This needs to be moved into a service/util, not hacked in here.
		String token = "Bearer " + context.getSecurityToken().getRawToken();
		headers.setAuthorization(token);

		return  addClientTraceDetailsToHeaders(headers);
	}

	public static ZenHttpHeaders addClientTraceDetailsToHeaders(ZenHttpHeaders headers)
	{
		if(TransactionContextHolder.isPresent())
		{
			TransactionContext context = TransactionContextHolder.getContext();
			headers.setSessionId(context.getSessionId());
			headers.setClientIp(context.getClientIp());
			headers.setDeviceId(context.getDeviceId());
			headers.setDeviceType(context.getDeviceType());
		}

		return  headers;
	}

	private static ZenHttpHeaders populateZenHeaders(ZenHttpHeaders headers, TransactionContext context)
	{
		String requestId = String.valueOf(UniqueIdentifierUtils.getUniqueId());
		String correlationId = context.getCorrelationId();

		if(correlationId == null)
		{
			correlationId = requestId;
		}

		headers.setRequestId(requestId);
		headers.setCorrelationId(context.getCorrelationId());
		headers.setSessionId(context.getSessionId());

		return headers;
	}

	private static ZenHttpHeaders populateZenHeaders(ZenHttpHeaders headers, EventContext context)
	{
		String requestId = String.valueOf(UniqueIdentifierUtils.getUniqueId());

		headers.setRequestId(requestId);
		headers.setCorrelationId(requestId);
		return headers;
	}

}
