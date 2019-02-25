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

package com.mikewerzen.zen.zenframework.transaction.context;

public class TransactionContext
{
	private final String serviceName;
	private final String serviceOperation;
	private final String serviceVersion;
	private final String serviceMethodName;

	private final String requestId;
	private final String correlationId;
	private final String sessionId;
	private final long internalTraceId;

	private final String clientIp;
	private final String sourceIp;

	private final String deviceId;
	private final String deviceType;

	public TransactionContext(String serviceName, String serviceOperation, String serviceVersion,
			String serviceMethodName, String requestId, String correlationId, String sessionId, long internalTraceId,
			String clientIp, String sourceIp, String deviceId, String deviceType)
	{
		this.serviceName = serviceName;
		this.serviceOperation = serviceOperation;
		this.serviceVersion = serviceVersion;
		this.serviceMethodName = serviceMethodName;
		this.requestId = requestId;
		this.correlationId = correlationId;
		this.sessionId = sessionId;
		this.internalTraceId = internalTraceId;
		this.sourceIp = sourceIp;
		this.clientIp = clientIp;
		this.deviceId = deviceId;
		this.deviceType = deviceType;
	}

	public String getServiceName()
	{
		return serviceName;
	}

	public String getServiceOperation()
	{
		return serviceOperation;
	}

	public String getServiceVersion()
	{
		return serviceVersion;
	}

	public String getServiceMethodName()
	{
		return serviceMethodName;
	}

	public String getRequestId()
	{
		return requestId;
	}

	public String getCorrelationId()
	{
		return correlationId;
	}

	public String getSessionId()
	{
		return sessionId;
	}

	public long getInternalTraceId()
	{
		return internalTraceId;
	}

	public String getClientIp()
	{
		return clientIp;
	}

	public String getSourceIp()
	{
		return clientIp;
	}

	public String getDeviceId()
	{
		return deviceId;
	}

	public String getDeviceType()
	{
		return deviceType;
	}
}
