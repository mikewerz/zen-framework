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

package com.mikewerzen.zen.zenframework.event.context;

public class EventContext
{
	private final String eventName;
	private final String eventVersion;
	private final String eventGroup;

	private final long internalTraceId;

	public EventContext(String eventName, String eventVersion, String eventGroup, long internalTraceId)
	{
		this.eventName = eventName;
		this.eventVersion = eventVersion;
		this.eventGroup = eventGroup;
		this.internalTraceId = internalTraceId;
	}

	public String getEventName()
	{
		return eventName;
	}

	public String getEventVersion()
	{
		return eventVersion;
	}

	public String getEventGroup()
	{
		return eventGroup;
	}

	public long getInternalTraceId()
	{
		return internalTraceId;
	}
}
