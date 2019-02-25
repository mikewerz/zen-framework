/*
 * Copyright 2019, Michael Werzen
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
 *//* Copyright  2019, Michael Werzen
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

package com.mikewerzen.zen.zenframework.rest;


import com.mikewerzen.zen.zenframework.exception.adapter.ThrowableAdapter;
import com.mikewerzen.zen.zenframework.exception.logic.InvalidRequestException;
import com.mikewerzen.zen.zenframework.exception.system.InternalException;
import com.mikewerzen.zen.zenframework.rest.exception.adapter.RestThrowableAdapter;
import com.mikewerzen.zen.zenframework.rest.request.ZenHttpHeaders;
import com.mikewerzen.zen.zenframework.rest.response.ZenError;
import com.mikewerzen.zen.zenframework.rest.response.ZenErrorResponse;
import com.mikewerzen.zen.zenframework.transaction.ZenTransaction;
import com.mikewerzen.zen.zenframework.transaction.context.TransactionContext;
import com.mikewerzen.zen.zenframework.transaction.context.TransactionContextHolder;
import com.mikewerzen.zen.zenframework.util.UniqueIdentifierUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;


import javax.servlet.http.HttpServletRequest;

@Component
public class RestServiceMapper
{

	@Autowired
	private HttpServletRequest httpServletRequest;

	public TransactionContext mapInboundRequestToContext(ZenTransaction transaction, RequestEntity requestEntity)
	{
		if (transaction == null)
		{
			throw new InternalException("Cannot map a null ZenTransaction annotation into a context.");
		}

		if (requestEntity == null)
		{
			throw new InternalException("Cannot map a null request into a context.");
		}

		if (requestEntity.getHeaders() == null)
		{
			throw new InvalidRequestException("Cannot map null headers into a transaction");
		}

		ZenHttpHeaders headers = new ZenHttpHeaders(requestEntity.getHeaders());

		long internalTraceId = UniqueIdentifierUtils.getUniqueId();

		return new TransactionContext(transaction.serviceName(), transaction.serviceOperation(),
				transaction.serviceVersion(), transaction.serviceMethodName(), headers.getRequestId(),
				headers.getCorrelationId(), headers.getSessionId(), internalTraceId, getClientIp(headers),
				getSourceIp(), headers.getDeviceId(), headers.getDeviceType());

	}

	public ResponseEntity<ZenErrorResponse> mapZenExceptionResponse(ThrowableAdapter details,
			Throwable throwable)
	{
		long internalTraceId = 0;

		if (TransactionContextHolder.getContextOptional().isPresent())
		{
			internalTraceId = TransactionContextHolder.getContextOptional().get().getInternalTraceId();
		}

		HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
		if (details instanceof RestThrowableAdapter)
		{
			status = ((RestThrowableAdapter) details).getHttpStatus(throwable);
		}

		ZenError zenError = new ZenError(status.value(), details.getExceptionCode(throwable),
				details.getExceptionMessage(throwable), internalTraceId);

		ZenErrorResponse response = new ZenErrorResponse(zenError);


		return new ResponseEntity<ZenErrorResponse>(response, status);
	}

	private String getClientIp(ZenHttpHeaders headers)
	{
		String clientIp = headers.getClientIp();
		if (httpServletRequest != null && (clientIp == null || clientIp.isEmpty()))
		{
			clientIp = httpServletRequest.getRemoteAddr();
		}
		return clientIp;
	}

	private String getSourceIp()
	{
		if (httpServletRequest != null)
		{
			return httpServletRequest.getRemoteAddr();
		}
		return null;
	}
}
