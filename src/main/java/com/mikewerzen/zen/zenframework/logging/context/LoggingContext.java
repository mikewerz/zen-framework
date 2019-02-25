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

package com.mikewerzen.zen.zenframework.logging.context;

import com.mikewerzen.zen.zenframework.logging.external.ExternalCallAttributes;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

public class LoggingContext
{
	private long startTime;

	private List<String> events = new ArrayList<>();
	private Map<String, Object> keyValueFields = new HashMap<>();
	private Map<String, Long> runtimes = new HashMap<>();
	private List<ExternalCallAttributes> externalLogAttributes = new ArrayList<>();

	private Throwable throwable;

	public LoggingContext()
	{
		startTime = System.currentTimeMillis();
	}

	public long getStartTime()
	{
		return startTime;
	}

	public void addEvent(String message)
	{
		events.add(message);
	}

	public List<String> getEvents()
	{
		return events;
	}

	public void addField(String key, Object value)
	{
		keyValueFields.put(key, value);
	}

	public void addFieldMasked(String key, String value)
	{
		keyValueFields.put(key, StringUtils.left(value, 4) + "****");
	}

	public void addFieldHashed(String key, String value)
	{
		keyValueFields.put(key, StringUtils.right(DigestUtils.sha1Hex(value), 16));
	}

	public void addEmailMasked(String key, String email)
	{
		StringBuilder builder = new StringBuilder();
		String[] splitEmail = StringUtils.split(email, "@");

		for(String part : splitEmail)
		{
			for(String subpart : StringUtils.split(part, "."))
			{
				builder.append(StringUtils.left(subpart, 3)).append("**").append(".");
			}

			builder.deleteCharAt(builder.length() - 1).append("@");
		}
		builder.deleteCharAt(builder.length() - 1);
		String masked = builder.toString();
		masked = masked.substring(0, masked.length() - 2);
		keyValueFields.put(key, masked);
	}

	public Set<Map.Entry<String, Object>> getKeyValueFields()
	{
		return keyValueFields.entrySet();
	}

	public void addRuntime(String methodName, long runtime)
	{
		runtimes.put(methodName, runtime);
	}

	public Set<Map.Entry<String, Long>> getRuntimes()
	{
		return runtimes.entrySet();
	}

	public void addExternalCall(ExternalCallAttributes attributes)
	{
		if (attributes != null)
		{
			externalLogAttributes.add(attributes);
		}
	}

	public List<ExternalCallAttributes> getExternalLogAttributes()
	{
		return externalLogAttributes;
	}

	public void addThrowable(Throwable throwable)
	{
		this.throwable = throwable;
	}

	public Throwable getThrowable()
	{
		return throwable;
	}

	public long getTotalExternalCallRuntime()
	{
		long externalRuntime = 0;

		for(ExternalCallAttributes attributes : externalLogAttributes)
		{
			externalRuntime += attributes.runtime;
		}

		return externalRuntime;
	}
}
