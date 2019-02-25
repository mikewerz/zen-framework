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

package com.mikewerzen.zen.zenframework.logging.external;

import com.mikewerzen.zen.zenframework.logging.context.LoggingContextHolder;
import com.mikewerzen.zen.zenframework.rest.request.ZenHttpHeaders;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpStatusCodeException;


public class ExternalCallAttributesBuilder
{
	private String systemName;
	private String serviceName;
	private String serviceVersion;
	private String serviceMethod;
	private String serviceOperation;
	private Integer httpStatusCode;
	private String externalResponseCode;
	private String externalResponseMessage;
	private String externalTraceId;

	private String requestTraceId;
	private String requestCorrelationId;
	private String requestSessionId;
	private String requestClientIp;
	private String requestDeviceId;
	private String requestDeviceType;

	private long startTime;
	private Throwable throwable;
	private boolean isSuccess = true;

	public ExternalCallAttributesBuilder(String systemName, String serviceName, String serviceMethod, String serviceOperation, String serviceVersion)
	{
		startTime = System.currentTimeMillis();
		this.systemName = systemName;
		this.serviceMethod = serviceMethod;
		this.serviceName = serviceName;
		this.serviceVersion = serviceVersion;
		this.serviceOperation = serviceOperation;
	}

	public ExternalCallAttributesBuilder setHeaderInformation(ZenHttpHeaders headers)
	{
		requestTraceId = headers.getRequestId();
		requestCorrelationId = headers.getCorrelationId();
		requestSessionId = headers.getSessionId();
		requestClientIp = headers.getClientIp();
		requestDeviceId = headers.getDeviceId();
		requestDeviceType = headers.getDeviceType();

		return this;
	}

	public ExternalCallAttributesBuilder setHeaderInformation(HttpHeaders headers)
	{
		return setHeaderInformation(new ZenHttpHeaders(headers));
	}

	public ExternalCallAttributesBuilder setHttpStatusCode(HttpStatus httpStatusCode)
	{
		if (httpStatusCode != null)
		{
			this.httpStatusCode = httpStatusCode.value();
		}

		return this;
	}

	public ExternalCallAttributesBuilder setHttpStatusCode(int httpStatusCode)
	{
		if(httpStatusCode != 0)
			this.httpStatusCode = httpStatusCode;

		return this;
	}

	public ExternalCallAttributesBuilder setExternalResponseCode(Object externalResponseCode)
	{
		this.externalResponseCode = String.valueOf(externalResponseCode);
		return this;
	}

	public ExternalCallAttributesBuilder setExternalResponseCode(String externalResponseCode)
	{
		this.externalResponseCode = externalResponseCode;
		return this;
	}

	public ExternalCallAttributesBuilder setExternalResponseMessage(String externalResponseMessage)
	{
		this.externalResponseMessage = externalResponseMessage;
		return this;
	}

	public ExternalCallAttributesBuilder setExternalTraceId(String externalTraceId)
	{
		this.externalTraceId = externalTraceId;
		return this;
	}

	public ExternalCallAttributesBuilder setExternalTraceId(Object externalTraceId)
	{
		this.externalTraceId = String.valueOf(externalTraceId);
		return this;
	}

	public ExternalCallAttributesBuilder setHttpStatusCodeException(HttpStatusCodeException exception)
	{
		setHttpStatusCode(exception.getStatusCode());
		setThrowable(exception);
		return this;
	}

	public ExternalCallAttributesBuilder setThrowable(Throwable throwable)
	{
		this.throwable = throwable;
		return this;
	}

	public ExternalCallAttributesBuilder setSuccess(boolean isSuccess)
	{
		this.isSuccess = isSuccess;
		return this;
	}

	public void buildAndCommit(boolean success)
	{
		isSuccess = success;
		LoggingContextHolder.getContextOptional().ifPresent(loggingContext -> loggingContext.addExternalCall(build()));
	}

	public void buildAndCommit()
	{
		LoggingContextHolder.getContextOptional().ifPresent(loggingContext -> loggingContext.addExternalCall(build()));
	}

	private ExternalCallAttributes build()
	{
		long endTime = System.currentTimeMillis();
		long runtime = endTime - startTime;
		return new ExternalCallAttributes(systemName, serviceName, serviceOperation, serviceVersion, serviceMethod,
				isSuccess, httpStatusCode, externalResponseCode, externalResponseMessage, requestTraceId,
				requestCorrelationId, requestSessionId, requestClientIp, requestDeviceId, requestDeviceType,
				externalTraceId, startTime, endTime, runtime, throwable);
	}
}