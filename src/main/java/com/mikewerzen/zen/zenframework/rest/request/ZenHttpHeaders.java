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

package com.mikewerzen.zen.zenframework.rest.request;

import org.springframework.http.HttpHeaders;

public class ZenHttpHeaders
{
	private static final String AUTHORIZATION = "Authorization";

	private static final String REQUEST_ID = "X-Request-ID";
	private static final String CORRELATION_ID = "X-Correlation-ID";
	private static final String SESSION_ID = "X-Session-ID";
	private static final String INTERNAL_TRACE_ID = "INTERNAL-TRACE-ID";

	private static final String CLIENT_IP = "X-Forwarded-For";

	private static final String DEVICE_ID = "Device-ID";
	private static final String DEVICE_TYPE = "Device-Type";


	private HttpHeaders headers;

	public ZenHttpHeaders()
	{
		this.headers = new HttpHeaders();
	}

	public ZenHttpHeaders(HttpHeaders headers)
	{
		this.headers = headers;
	}

	public String getAuthorization()
	{
		return headers.getFirst(AUTHORIZATION);
	}

	public void setAuthorization(String value)
	{
		headers.add(AUTHORIZATION, value);
	}

	public String getRequestId()
	{
		return headers.getFirst(REQUEST_ID);
	}

	public void setRequestId(String value)
	{
		headers.add(REQUEST_ID, value);
	}

	public String getCorrelationId()
	{
		return headers.getFirst(CORRELATION_ID);
	}

	public void setCorrelationId(String value)
	{
		headers.add(CORRELATION_ID, value);
	}

	public String getSessionId()
	{
		return headers.getFirst(SESSION_ID);
	}

	public void setSessionId(String value)
	{
		headers.add(SESSION_ID, value);
	}

	public String getInternalTraceId()
	{
		return headers.getFirst(INTERNAL_TRACE_ID);
	}

	public void setInternalTraceId(String value)
	{
		headers.set(INTERNAL_TRACE_ID, value);
	}

	public String getClientIp()
	{
		return headers.getFirst(CLIENT_IP);
	}

	public void setClientIp(String value)
	{
		headers.set(CLIENT_IP, value);
	}

	public String getDeviceId()
	{
		return headers.getFirst(DEVICE_ID);
	}

	public void setDeviceId(String value)
	{
		headers.set(DEVICE_ID, value);
	}

	public String getDeviceType()
	{
		return headers.getFirst(DEVICE_TYPE);
	}

	public void setDeviceType(String value)
	{
		headers.set(DEVICE_TYPE, value);
	}

	public HttpHeaders getHeaders()
	{
		return headers;
	}
}
