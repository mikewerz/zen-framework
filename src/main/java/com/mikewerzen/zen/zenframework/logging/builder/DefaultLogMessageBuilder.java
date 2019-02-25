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

package com.mikewerzen.zen.zenframework.logging.builder;

import com.google.gson.*;
import com.mikewerzen.zen.zenframework.event.context.EventContext;
import com.mikewerzen.zen.zenframework.event.context.EventContextHolder;
import com.mikewerzen.zen.zenframework.exception.adapter.ExternalExceptionAdapter;
import com.mikewerzen.zen.zenframework.exception.adapter.ThrowableAdapter;
import com.mikewerzen.zen.zenframework.exception.adapter.ThrowableAdapterFinder;
import com.mikewerzen.zen.zenframework.logging.context.LoggingContext;
import com.mikewerzen.zen.zenframework.logging.context.LoggingContextHolder;
import com.mikewerzen.zen.zenframework.logging.export.LogDataExporter;
import com.mikewerzen.zen.zenframework.logging.external.ExternalCallAttributes;
import com.mikewerzen.zen.zenframework.rest.exception.adapter.RestThrowableAdapter;
import com.mikewerzen.zen.zenframework.rest.response.ZenError;
import com.mikewerzen.zen.zenframework.rest.response.ZenErrorResponse;
import com.mikewerzen.zen.zenframework.security.context.SecurityContext;
import com.mikewerzen.zen.zenframework.security.context.SecurityContextHolder;
import com.mikewerzen.zen.zenframework.security.provider.zen.jwt.token.JWTToken;
import com.mikewerzen.zen.zenframework.transaction.context.TransactionContext;
import com.mikewerzen.zen.zenframework.transaction.context.TransactionContextHolder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Component
public class DefaultLogMessageBuilder implements LogMessageBuilder
{

	private static final Logger logger = LogManager.getLogger(DefaultLogMessageBuilder.class);

	private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

	@Autowired
	private LogDataExporter logDataExporter;

	public String buildTransactionLog(Object response)
	{
		ResponseEntity<?> responseEntity = convertResponseIntoResponseEntity(response);

		JsonObject logMessage = new JsonObject();



		if (TransactionContextHolder.getContextOptional().isPresent())
		{
			addTransactionContextFields(TransactionContextHolder.getContextOptional(), logMessage);
		}

		if (SecurityContextHolder.getContextOptional().isPresent())
		{
			addSecurityContextFields(SecurityContextHolder.getContextOptional(), logMessage);
		}

		if (LoggingContextHolder.getContextOptional().isPresent())
		{
			addLoggingContextFields(LoggingContextHolder.getContextOptional(), logMessage);
		}
		else if (doesResponseContainAnError(responseEntity))
		{
			addResponseFields(responseEntity.getBody(), logMessage);
		}

		if (responseEntity != null && !logMessage.has("response"))
		{
			JsonObject res = new JsonObject();
			addIfNotNull(res, "httpStatusCode", responseEntity.getStatusCodeValue());
			logMessage.add("response", res);
		}

		return gson.toJson(logMessage);
	}

	@Override
	public String buildEventLog(Object response)
	{
		JsonObject logMessage = new JsonObject();

		if(EventContextHolder.getContextOptional().isPresent())
		{
			addEventContextFields(logMessage, EventContextHolder.getContextOptional());
		}

		if (LoggingContextHolder.getContextOptional().isPresent())
		{
			addLoggingContextFields(LoggingContextHolder.getContextOptional(), logMessage);
		}

		return gson.toJson(logMessage);
	}

	private void addEventContextFields(JsonObject logMessage, Optional<EventContext> eventContextOptional)
	{
		EventContext eventContext = eventContextOptional.get();
		addIfNotNull(logMessage, "eventName", eventContext.getEventName());
		addIfNotNull(logMessage, "eventVersion", eventContext.getEventVersion());
		addIfNotNull(logMessage, "eventGroup", eventContext.getEventGroup());

		JsonObject trace = new JsonObject();
		addIfNotNull(trace, "internalTraceId", eventContext.getInternalTraceId());
		logMessage.add("trace", trace);
	}


	private ResponseEntity<?> convertResponseIntoResponseEntity(Object response)
	{
		if(response instanceof ResponseEntity)
		{
			return (ResponseEntity) response;
		}

		logger.error("Attempting to log details from a class that is not a ResponseEntity. Log data may be incomplete.");
		return null;
	}

	private void addSecurityContextFields(Optional<SecurityContext> securityContextOptional, JsonObject logMessage)
	{
		SecurityContext securityContext = securityContextOptional.get();

		JsonObject security = new JsonObject();

		JsonObject auth = new JsonObject();
		addIfNotNull(auth, "type", securityContext.getSecurityToken().getClass().getSimpleName());
		addIfNotNull(auth, "id", securityContext.getSecurityToken().getTokenId());
		security.add("auth", auth);

		JsonObject user = new JsonObject();
		addIfNotNull(user, "id", securityContext.getSecurityToken().getUserId());
		addIfNotNull(user, "appName", securityContext.getSecurityToken().getAppName());
		addIfNotNull(user, "username", securityContext.getSecurityToken().getUsername());
		security.add("user", user);


		if(securityContext.getSecurityToken() instanceof JWTToken)
		{
			JWTToken token = (JWTToken) securityContext.getSecurityToken();
			addSecurityRoles(user, securityContext);
			addSecurityEvents(user, securityContext);

		}

		logMessage.add("security", security);


	}

	private void addSecurityRoles(JsonObject user, SecurityContext securityContext)
	{
		if (securityContext.getSecurityToken().getRoles() != null)
		{
			JsonArray rolesArray = new JsonArray();
			for (int i = 0; i < 3 && i < securityContext.getSecurityToken().getRoles().length; i++)
			{
				rolesArray.add(securityContext.getSecurityToken().getRoles()[i].toString());
			}
			user.add("roles", rolesArray);
		}
	}

	private void addSecurityEvents(JsonObject user, SecurityContext securityContext)
	{
		if (securityContext.getSecurityToken().getEvents() != null)
		{
			JsonArray rolesArray = new JsonArray();
			for (int i = 0; i < 3 && i < securityContext.getSecurityToken().getEvents().length; i++)
			{
				rolesArray.add(securityContext.getSecurityToken().getEvents()[i].toString());
			}
			user.add("events", rolesArray);
		}
	}

	private void addTransactionContextFields(Optional<TransactionContext> transactionContextOptional, JsonObject logMessage)
	{
		TransactionContext transactionContext = transactionContextOptional.get();

		addIfNotNull(logMessage, "serviceName", transactionContext.getServiceName());
		addIfNotNull(logMessage, "serviceOperation", transactionContext.getServiceOperation());
		addIfNotNull(logMessage, "serviceVersion", transactionContext.getServiceVersion());
		addIfNotNull(logMessage, "serviceMethodName", transactionContext.getServiceMethodName());


		JsonObject trace = new JsonObject();

		JsonObject request = new JsonObject();
		addIfNotNull(request, "requestId", transactionContext.getRequestId());
		addIfNotNull(request, "correlationId", transactionContext.getCorrelationId());
		addIfNotNull(request, "sessionId", transactionContext.getSessionId());
		addIfNotNull(request, "sourceIp", transactionContext.getSourceIp());
		addIfNotNull(request, "clientIp", transactionContext.getClientIp());
		addIfNotNull(request, "deviceId", transactionContext.getDeviceId());
		addIfNotNull(request, "deviceType", transactionContext.getDeviceType());
		trace.add("request", request);

		JsonObject response = new JsonObject();
		addIfNotNull(response, "internalTraceId", transactionContext.getInternalTraceId());
		trace.add("response", response);

		logMessage.add("trace", trace);

	}

	private void addLoggingContextFields(Optional<LoggingContext> loggingContextOptional, JsonObject logMessage)
	{
		LoggingContext loggingContext = loggingContextOptional.get();

		long endTime = System.currentTimeMillis();

		JsonObject timings = new JsonObject();
		addIfNotNull(timings, "startTime", loggingContext.getStartTime());
		addIfNotNull(timings, "endTime", endTime);
		addIfNotNull(timings, "runtime", (endTime - loggingContext.getStartTime()));
		timings.add("runtimes", getRuntimes(loggingContext.getRuntimes()));
		logMessage.add("timings", timings);

		logMessage.add("events", getEventJson(loggingContext.getEvents()));
		logMessage.add("keyValue", getKeyValueJson(loggingContext.getKeyValueFields()));
		logMessage.add("externalCalls", getExternalCallsJson(loggingContext.getExternalLogAttributes()));

		JsonObject response = new JsonObject();
		Throwable throwable = loggingContext.getThrowable();
		if (throwable != null)
		{
			ThrowableAdapter adapter = ThrowableAdapterFinder.getThrowableAdapter(throwable);


			addIfNotNull(response, "exceptionCode", adapter.getExceptionCode(throwable));
			addIfNotNull(response, "exceptionMessage", adapter.getExceptionMessage(throwable));

			if(adapter instanceof ExternalExceptionAdapter)
			{
				ExternalExceptionAdapter externalExceptionAdapter = (ExternalExceptionAdapter) adapter;
				addIfNotNull(response, "externalExceptionCode", externalExceptionAdapter.getExternalExceptionCode(throwable));
				addIfNotNull(response, "externalExceptionMessage", externalExceptionAdapter.getExternalExceptionMessage(throwable));
			}

			if(adapter instanceof RestThrowableAdapter)
			{
				addIfNotNull(response, "httpStatusCode", ((RestThrowableAdapter) adapter).getHttpStatus(throwable));
			}

			logMessage.add("response", response);
			logMessage.add("stackTrace", getStackTraceJson(loggingContext.getThrowable()));
		}
	}

	private boolean doesResponseContainAnError(ResponseEntity<?> response)
	{
		return response != null && response.getBody() != null &&
				response.getBody() instanceof ZenErrorResponse && ((ZenErrorResponse) response.getBody()).getError() != null;
	}

	private void addIfNotNull(JsonObject object, String key, String value)
	{
		if (key != null && value != null && !value.isEmpty())
		{
			object.addProperty(key, value);
		}
	}

	private void addIfNotNull(JsonObject object, String key, Object value)
	{
		if (key != null && value != null)
		{
			object.addProperty(key, value.toString());
		}
	}

	private void addIfNotNull(JsonObject object, String key, Number value)
	{
		if (key != null && value != null)
		{
			object.addProperty(key, value);
		}
	}

	private void addIfNotNull(JsonObject object, String key, Boolean value)
	{
		if (key != null && value != null)
		{
			object.addProperty(key, value);
		}
	}

	private JsonElement getEventJson(List<String> events)
	{
		if (events != null && events.size() > 0)
		{
			JsonArray eventJson = new JsonArray();
			for (String event : events)
			{
				if (event != null && !event.isEmpty())
				{
					eventJson.add(event);
				}
			}
			return eventJson;
		}

		return null;
	}

	private JsonObject getKeyValueJson(Set<Map.Entry<String, Object>> kvPairs)
	{
		if (kvPairs != null && kvPairs.size() > 0)
		{
			JsonObject kvJson = new JsonObject();
			for (Map.Entry<String, Object> pair : kvPairs)
			{
				addIfNotNull(kvJson, pair.getKey(), pair.getValue());
			}

			return kvJson;
		}

		return null;
	}

	private JsonObject getRuntimes(Set<Map.Entry<String, Long>> runtimePairs)
	{
		if (runtimePairs != null && runtimePairs.size() > 0)
		{
			JsonObject runtimesJson = new JsonObject();
			for (Map.Entry<String, Long> pair : runtimePairs)
			{
				addIfNotNull(runtimesJson, pair.getKey(), pair.getValue().longValue());
			}

			return runtimesJson;
		}

		return null;
	}

	private JsonArray getExternalCallsJson(List<ExternalCallAttributes> externalCalls)
	{
		if(externalCalls != null && externalCalls.size() > 0)
		{
			JsonArray arrayOfCalls = new JsonArray();
			for(ExternalCallAttributes attributes : externalCalls)
			{
				arrayOfCalls.add(buildExternalCallJson(attributes));
			}
			return arrayOfCalls;
		}

		return null;
	}

	private JsonObject buildExternalCallJson(ExternalCallAttributes attributes)
	{
		JsonObject externalCall = new JsonObject();
		addIfNotNull(externalCall, "systemName", attributes.systemName);
		addIfNotNull(externalCall, "serviceName", attributes.serviceName);
		addIfNotNull(externalCall, "serviceMethod", attributes.serviceMethod);
		addIfNotNull(externalCall, "serviceOperation", attributes.serviceOperation);
		addIfNotNull(externalCall, "serviceVersion", attributes.serviceVersion);
		addIfNotNull(externalCall, "success", attributes.success);

		JsonObject trace = new JsonObject();
		JsonObject request = new JsonObject();
		addIfNotNull(request, "traceId", attributes.requestTraceId);
		addIfNotNull(request, "correlationId", attributes.requestCorrelationId);
		addIfNotNull(request, "sessionId", attributes.requestSessionId);
		addIfNotNull(request, "clientIp", attributes.requestDeviceId);
		addIfNotNull(request, "deviceId", attributes.requestDeviceId);
		addIfNotNull(request, "deviceType", attributes.requestDeviceType);
		trace.add("request", request);

		JsonObject responseTrace = new JsonObject();
		addIfNotNull(responseTrace, "externalTraceId", attributes.externalTraceId);
		trace.add("response", responseTrace);

		externalCall.add("trace", trace);


		JsonObject timings = new JsonObject();
		addIfNotNull(timings, "startTime", attributes.startTime);
		addIfNotNull(timings, "endTime", attributes.endTime);
		addIfNotNull(timings, "runtime", attributes.runtime);
		externalCall.add("externalTimings", timings);

		JsonObject response = new JsonObject();
		addIfNotNull(response, "httpStatusCode", attributes.httpStatusCode);
		addIfNotNull(response, "externalResponseCode", attributes.externalResponseCode);
		addIfNotNull(response, "externalResponseMessage", attributes.externalResponseMessage);
		externalCall.add("externalResponse", response);

		if(attributes.throwable != null)
		{
			externalCall.add("clientStackTrace", getStackTraceJson(attributes.throwable));
		}
		return externalCall;
	}

	private JsonObject getStackTraceJson(Throwable throwable)
	{
		JsonObject stackTrace = new JsonObject();
		stackTrace.addProperty("class", throwable.getClass().getCanonicalName());
		stackTrace.addProperty("message", throwable.getMessage());

		JsonArray stackTraceArray = new JsonArray();
		for (int i = 0; i < 3 && i < throwable.getStackTrace().length; i++)
		{
			stackTraceArray.add(throwable.getStackTrace()[i].toString());
		}
		stackTrace.add("trace", stackTraceArray);

		if (throwable.getCause() != null)
		{
			stackTrace.add("causedBy", getStackTraceJson(throwable.getCause()));
		}

		return stackTrace;
	}

	private void addResponseFields(Object response, JsonObject logMessage)
	{
		JsonObject jsonResponse = new JsonObject();
		if (response instanceof ZenErrorResponse)
		{
			ZenError zenError = ((ZenErrorResponse) response).getError();
			addIfNotNull(logMessage, "httpStatusCode", zenError.httpStatus);
			addIfNotNull(logMessage, "exceptionCode", zenError.errorCode);
			addIfNotNull(logMessage, "exceptionMessage", zenError.errorMessage);
		}

		logMessage.add("response", jsonResponse);
	}

}
