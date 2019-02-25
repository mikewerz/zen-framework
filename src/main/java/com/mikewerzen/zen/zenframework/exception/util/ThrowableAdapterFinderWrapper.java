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

package com.mikewerzen.zen.zenframework.exception.util;

import com.mikewerzen.zen.zenframework.exception.adapter.ThrowableAdapter;
import com.mikewerzen.zen.zenframework.exception.adapter.ThrowableAdapterFinder;
import com.mikewerzen.zen.zenframework.logging.annotation.LogRuntime;
import org.springframework.stereotype.Component;

/**
 * Provides a Spring-Managed wrapper around ThrowableAdapterFinder.
 */
@Component
public class ThrowableAdapterFinderWrapper
{

	public void registerAdapter(ThrowableAdapter adapter, Class... classes)
	{
		ThrowableAdapterFinder.registerAdapter(adapter, classes);
	}

	public void registerDefaultAdapter(ThrowableAdapter adapter, Class... classes)
	{
		ThrowableAdapterFinder.registerDefaultAdapter(adapter, classes);
	}

	public void registerFallbackAdapter(ThrowableAdapter adapter)
	{
		ThrowableAdapterFinder.registerFallbackAdapter(adapter);
	}

	@LogRuntime(debugOnly = false)
	public ThrowableAdapter getThrowableAdapter(Throwable throwable)
	{
		return ThrowableAdapterFinder.getThrowableAdapter(throwable);
	}
}
