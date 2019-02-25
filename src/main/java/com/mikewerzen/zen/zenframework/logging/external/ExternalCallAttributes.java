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

package com.mikewerzen.zen.zenframework.logging.external;

public class ExternalCallAttributes
{
	public final String systemName;
	public final String serviceName;
	public final String serviceOperation;
	public final String serviceVersion;
	public final String serviceMethod;

	public final Boolean success;
	public final Integer httpStatusCode;
	public final String externalResponseCode;
	public final String externalResponseMessage;
	public final String externalTraceId;

	public final String requestTraceId;
	public final String requestCorrelationId;
	public final String requestSessionId;
	public final String requestClientIp;
	public final String requestDeviceId;
	public final String requestDeviceType;

	public final Throwable throwable;

	public final long startTime;
	public final long endTime;
	public final long runtime;

	ExternalCallAttributes(String systemName, String serviceName, String serviceOperation, String serviceVersion,
			String serviceMethod, Boolean success, Integer httpStatusCode, String externalResponseCode,
			String externalResponseMessage, String requestTraceId, String requestCorrelationId, String
			requestSessionId, String requestClientIp, String requestDeviceId, String requestDeviceType, String
			externalTraceId, long startTime, long endTime, long runtime, Throwable throwable)
	{
		this.systemName = systemName;
		this.serviceName = serviceName;
		this.serviceOperation = serviceOperation;
		this.serviceVersion = serviceVersion;
		this.serviceMethod = serviceMethod;

		this.success = success;
		this.httpStatusCode = httpStatusCode;
		this.externalResponseCode = externalResponseCode;
		this.externalResponseMessage = externalResponseMessage;
		this.requestTraceId = requestTraceId;

		this.requestCorrelationId = requestCorrelationId;
		this.requestSessionId = requestSessionId;
		this.externalTraceId = externalTraceId;
		this.requestClientIp = requestClientIp;
		this.requestDeviceId = requestDeviceId;
		this.requestDeviceType = requestDeviceType;

		this.startTime = startTime;
		this.endTime = endTime;
		this.runtime = runtime;
		this.throwable = throwable;
	}
}
