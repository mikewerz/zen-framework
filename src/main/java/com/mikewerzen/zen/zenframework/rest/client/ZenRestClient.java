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

package com.mikewerzen.zen.zenframework.rest.client;


import com.mikewerzen.zen.zenframework.exception.system.ExternalException;
import com.mikewerzen.zen.zenframework.exception.system.RethrownExternalException;
import com.mikewerzen.zen.zenframework.logging.external.ExternalCallAttributesBuilder;
import com.mikewerzen.zen.zenframework.rest.response.ZenError;
import com.mikewerzen.zen.zenframework.rest.response.ZenErrorResponse;
import com.mikewerzen.zen.zenframework.rest.util.RestUtils;
import com.mikewerzen.zen.zenframework.security.context.SecurityContextHolder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;


public class ZenRestClient extends AbstractRestClient<ZenErrorResponse>
{
	public ZenRestClient(String serviceName)
	{
		this(null, serviceName);
	}

	public ZenRestClient(String systemName, String serviceName)
	{
		this(systemName, serviceName, null);
	}

	public ZenRestClient(String systemName, String serviceName, String serviceVersion)
	{
		super(systemName, serviceName, serviceVersion, ZenErrorResponse.class);
	}

	public <Request, SuccessfulResponse> SuccessfulResponse post(String methodName, String url, Request request,
			Class<SuccessfulResponse> responseClass)
	{
		return super.execute(methodName, request, url, HttpMethod.POST, responseClass);
	}

	public <Request, SuccessfulResponse> SuccessfulResponse get(String methodName, String url,
			Class<SuccessfulResponse> responseClass)
	{
		return super.execute(methodName, null, url, HttpMethod.GET, responseClass);
	}

	public <Request, SuccessfulResponse> SuccessfulResponse put(String methodName, String url, Request request,
			Class<SuccessfulResponse> responseClass)
	{
		return super.execute(methodName, request, url, HttpMethod.PUT, responseClass);
	}

	public <Request, SuccessfulResponse> SuccessfulResponse patch(String methodName, String url, Request request,
			Class<SuccessfulResponse> responseClass)
	{
		return super.execute(methodName, request, url, HttpMethod.POST, responseClass);
	}

	public <Request, SuccessfulResponse> SuccessfulResponse delete(String methodName, String url, Request request,
			Class<SuccessfulResponse> responseClass)
	{
		return super.execute(methodName, request, url, HttpMethod.DELETE, responseClass);
	}

	@Override
	protected HttpHeaders buildHttpHeaders()
	{
		if (SecurityContextHolder.isPresent())
		{
			return RestUtils.buildHttpHeadersWithAuth();
		}

		return RestUtils.buildHttpHeaders();
	}

	@Override
	protected ExternalException convertErrorIntoException(ZenErrorResponse zenErrorResponse)
	{
		ZenError error = zenErrorResponse.getError();

		HttpStatus status = HttpStatus.valueOf(error.httpStatus);
		if(status.is4xxClientError())
		{
			return new RethrownExternalException(error.httpStatus, error.errorCode, error.errorMessage);
		}

		return new ExternalException(String.valueOf(error.errorCode), error.errorMessage);
	}

	@Override
	protected void addErrorAttributes(ExternalCallAttributesBuilder builder,
			ZenErrorResponse zenErrorResponse)
	{
		ZenError error = zenErrorResponse.getError();
		builder.setExternalResponseCode(error.errorCode);
		builder.setExternalResponseMessage(error.errorMessage);
		builder.setExternalTraceId(error.internalErrorId);
	}
}
