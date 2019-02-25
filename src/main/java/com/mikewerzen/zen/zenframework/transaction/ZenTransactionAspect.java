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
 */

package com.mikewerzen.zen.zenframework.transaction;

import com.mikewerzen.zen.zenframework.exception.adapter.ThrowableAdapter;
import com.mikewerzen.zen.zenframework.exception.system.InternalException;
import com.mikewerzen.zen.zenframework.exception.util.ThrowableAdapterFinderWrapper;
import com.mikewerzen.zen.zenframework.logging.ZenLogManager;
import com.mikewerzen.zen.zenframework.logging.context.LoggingContext;
import com.mikewerzen.zen.zenframework.logging.context.LoggingContextHolder;
import com.mikewerzen.zen.zenframework.rest.RestServiceMapper;
import com.mikewerzen.zen.zenframework.rest.exception.adapter.RestThrowableAdapter;
import com.mikewerzen.zen.zenframework.rest.response.ZenErrorResponse;
import com.mikewerzen.zen.zenframework.security.SecurityManager;
import com.mikewerzen.zen.zenframework.security.context.SecurityContextHolder;
import com.mikewerzen.zen.zenframework.transaction.context.TransactionContextHolder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;



@Aspect
@Component
public class ZenTransactionAspect
{
	Logger logger = LogManager.getLogger(ZenTransactionAspect.class);

	@Autowired
	private SecurityManager securityManager;

	@Autowired
	private RestServiceMapper restServiceMapper;

	@Autowired
	private ThrowableAdapterFinderWrapper throwableAdapterFinderWrapper;

	@Autowired
	private ZenLogManager logManager;

	@Pointcut("execution(public * *(..))")
	void anyPublicMethod()
	{
	}

	@Around("anyPublicMethod() && @annotation(zenTransaction)")
	public Object zenTransaction(ProceedingJoinPoint proceedingJoinPoint, ZenTransaction zenTransaction)
	{
		ResponseEntity<?> response = null;

		try
		{
			try
			{
				beginTransaction(zenTransaction, getRequestEntity(proceedingJoinPoint));

				response = getResponseEntity(proceedingJoinPoint, proceedingJoinPoint.proceed());

			}
			catch (Throwable throwable)
			{
				logger.error("Caught in expected block", throwable);
				response = handleError(throwable);
			}
			finally
			{
				endTransaction(response);
			}
		}
		catch (Throwable throwable)
		{
			logger.error("Exception occurred while ending previous transaction", throwable);
			response = handleError(throwable);
		}

		return response;
	}

	private void beginTransaction(ZenTransaction transaction, RequestEntity requestEntity)
	{
		LoggingContextHolder.setContext(new LoggingContext());
		TransactionContextHolder.setContext(restServiceMapper.mapInboundRequestToContext(transaction, requestEntity));

		securityManager.secureRequest(transaction, requestEntity);
	}

	private void endTransaction(ResponseEntity<?> response)
	{
		logManager.logTransaction(response);

		LoggingContextHolder.clearContext();
		TransactionContextHolder.clearContext();
		SecurityContextHolder.clearContext();
	}

	public ResponseEntity<ZenErrorResponse> handleError(Throwable throwable)
	{
		ThrowableAdapter throwableAdapter = throwableAdapterFinderWrapper.getThrowableAdapter(throwable);
		LoggingContextHolder.getContextOptional().ifPresent(context -> context.addThrowable(throwable));
		return restServiceMapper.mapZenExceptionResponse((RestThrowableAdapter) throwableAdapter, throwable);
	}


	private RequestEntity getRequestEntity(ProceedingJoinPoint joinPoint)
	{
		if (joinPoint != null && joinPoint.getArgs() != null && joinPoint.getArgs().length > 0)
		{
			for (Object object : joinPoint.getArgs())
			{
				if (object instanceof RequestEntity<?>)
				{
					return (RequestEntity) object;
				}
			}
		}

		throw new InternalException("Could not apply @ZenTransaction to a serviceMethodName without a RequestEntity parameter");
	}

	private ResponseEntity<?> getResponseEntity(ProceedingJoinPoint joinPoint, Object methodCallReponse)
	{
		if (methodCallReponse instanceof ResponseEntity)
		{
			try
			{
				return (ResponseEntity<?>) methodCallReponse;
			}
			catch (ClassCastException classCastException)
			{
				throw new InternalException("ClassCastException attempting to convert a ResponseEntity into a ResponseEntity.", classCastException);
			}
		}

		throw new InternalException("Could not apply @ZenTransaction to a serviceMethodName without a ResponseEntity<?> response.");
	}

}
