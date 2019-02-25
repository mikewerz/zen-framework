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

package com.mikewerzen.zen.zenframework.logging.utils;

import com.mikewerzen.zen.zenframework.logging.context.LoggingContextHolder;

public class LoggingUtils
{

	private static final String AUTHENTICATED = "authenticated";
	private static final String AUTHORIZATION = "authenticated";

	private static final String PASSED = "passed";
	private static final String SKIPPED = "skipped";
	private static final String FAILED = "failed";

	public static void logAuthenticationPassed()
	{
		LoggingContextHolder
				.getContextOptional().ifPresent(loggingContext -> loggingContext.addField(AUTHENTICATED, PASSED));
	}

	public static void logAuthenticationSkipped()
	{
		LoggingContextHolder.getContextOptional().ifPresent(loggingContext -> loggingContext.addField(AUTHENTICATED, SKIPPED));
	}

	public static void logAuthenticationFailed()
	{
		LoggingContextHolder.getContextOptional().ifPresent(loggingContext -> loggingContext.addField(AUTHENTICATED, FAILED));
	}

	public static void logAuthorizationPassed()
	{
		LoggingContextHolder.getContextOptional().ifPresent(loggingContext -> loggingContext.addField(AUTHORIZATION, PASSED));
	}

	public static void logAuthorizationSkipped()
	{
		LoggingContextHolder.getContextOptional().ifPresent(loggingContext -> loggingContext.addField(AUTHORIZATION, SKIPPED));
	}

	public static void logAuthorizationFailed()
	{
		LoggingContextHolder.getContextOptional().ifPresent(loggingContext -> loggingContext.addField(AUTHORIZATION, FAILED));
	}
}
