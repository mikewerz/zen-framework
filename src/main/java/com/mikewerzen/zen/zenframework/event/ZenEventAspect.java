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

package com.mikewerzen.zen.zenframework.event;

import com.mikewerzen.zen.zenframework.event.context.EventContext;
import com.mikewerzen.zen.zenframework.event.context.EventContextHolder;
import com.mikewerzen.zen.zenframework.exception.util.ThrowableAdapterFinderWrapper;
import com.mikewerzen.zen.zenframework.logging.ZenLogManager;
import com.mikewerzen.zen.zenframework.logging.context.LoggingContext;
import com.mikewerzen.zen.zenframework.logging.context.LoggingContextHolder;
import com.mikewerzen.zen.zenframework.util.UniqueIdentifierUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ZenEventAspect
{
	Logger logger = LogManager.getLogger(ZenEventAspect.class);

	@Autowired
	private ThrowableAdapterFinderWrapper throwableAdapterFinderWrapper;

	@Autowired
	private ZenLogManager logManager;

	@Pointcut("execution(public * *(..))")
	void anyPublicMethod()
	{
	}

	@Around("anyPublicMethod() && @annotation(zenEvent)")
	public Object zenTransaction(ProceedingJoinPoint proceedingJoinPoint, ZenEvent zenEvent) throws Throwable
	{
		boolean wasLoggingContextSet = false;
		boolean wasEventContextSet = false;

		Object response = null;
		Throwable throwable = null;

		try
		{
			try
			{
				wasLoggingContextSet = initializeLoggingContext(wasLoggingContextSet);
				wasEventContextSet = initializeEventContext(zenEvent, wasEventContextSet);

				response = proceedingJoinPoint.proceed();
			}
			catch (Throwable t)
			{
				handleThrowable(zenEvent, t);
				throwable = t;
			}
			finally
			{
				endEvent(wasLoggingContextSet, wasEventContextSet, response);
			}
		}
		catch (Throwable t)
		{
			logger.error("Exception occurred while attempting to end an Event: " + zenEvent.eventName(), t);
		}

		if (throwable != null)
		{
			throw throwable;
		}

		return response;
	}

	private boolean initializeLoggingContext(boolean wasLoggingContextSet)
	{
		if (!LoggingContextHolder.getContextOptional().isPresent())
		{
			LoggingContextHolder.setContext(new LoggingContext());
			wasLoggingContextSet = true;
		}
		return wasLoggingContextSet;
	}

	private boolean initializeEventContext(ZenEvent zenEvent, boolean wasEventContextSet)
	{
		if (!EventContextHolder.getContextOptional().isPresent())
		{
			long eventId = UniqueIdentifierUtils.getUniqueId();
			EventContext context = new EventContext(zenEvent.eventName(), zenEvent.eventVersion(), zenEvent.eventGroup(), eventId);
			EventContextHolder.setContext(context);
			wasEventContextSet = true;
		}
		return wasEventContextSet;
	}

	private void handleThrowable(ZenEvent zenEvent, Throwable throwable)
	{
		logger.error("An exception was thrown while processing an event: " + zenEvent.eventName(), throwable);
		LoggingContextHolder.getContextOptional().ifPresent(loggingContext -> loggingContext.addThrowable(throwable));
	}

	private void endEvent(boolean wasLoggingContextSet, boolean wasEventContextSet, Object response)
	{
		logManager.logEvent(response);

		if (wasLoggingContextSet)
		{
			LoggingContextHolder.clearContext();
		}
		if (wasEventContextSet)
		{
			EventContextHolder.clearContext();
		}
	}
}
