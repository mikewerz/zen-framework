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

package com.mikewerzen.zen.zenframework.exception.adapter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ThrowableAdapterFinder
{
	private static final Logger logger = LogManager.getLogger(ThrowableAdapterFinder.class);

	private static final Map<String, ThrowableAdapter> exceptionsToAdapters = new ConcurrentHashMap<>();

	private static ThrowableAdapter fallbackThrowableAdapter = null;

	private ThrowableAdapterFinder()
	{

	}

	public static void registerAdapter(ThrowableAdapter adapter, Class... classes)
	{
		for (Class exception : classes)
		{
			exceptionsToAdapters.put(exception.getCanonicalName(), adapter);
		}
	}

	public static void registerDefaultAdapter(ThrowableAdapter adapter, Class... classes)
	{
		for (Class exception : classes)
		{
			String exceptionClassName = exception.getCanonicalName();

			if(!exceptionsToAdapters.containsKey(exceptionClassName))
			{
				exceptionsToAdapters.put(exceptionClassName, adapter);
			}
		}
	}

	public static void registerFallbackAdapter(ThrowableAdapter adapter)
	{
		fallbackThrowableAdapter = adapter;
	}

	public static ThrowableAdapter getThrowableAdapter(Throwable throwable)
	{
		Class clazz = throwable.getClass();

		if(exceptionsToAdapters.containsKey(clazz.getCanonicalName()))
		{
			return exceptionsToAdapters.get(clazz.getCanonicalName());
		}
		else
		{
			ThrowableAdapter adapter = null;
			clazz = clazz.getSuperclass();

			while (clazz != null)
			{
				if (exceptionsToAdapters.containsKey(clazz.getCanonicalName()))
				{
					adapter = exceptionsToAdapters.get(clazz.getCanonicalName());
					break;
				}

				clazz = clazz.getSuperclass();
			}

			if(adapter == null)
			{
				adapter = fallbackThrowableAdapter;
			}

			if(adapter == null)
			{
				adapter = new FallbackThrowableAdapter();
			}

			// Register adapter so we do not need to look them up again
			registerAdapter(adapter, throwable.getClass());
			logger.debug("Inserting Entry in ThrowableAdapterFinder for Throwable: " + throwable.getClass().getSimpleName() + " to Adapter: " + adapter.getClass().getSimpleName());

			return adapter;
		}
	}
}
