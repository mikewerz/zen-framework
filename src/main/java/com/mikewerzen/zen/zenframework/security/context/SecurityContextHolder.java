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

package com.mikewerzen.zen.zenframework.security.context;

import com.mikewerzen.zen.zenframework.exception.system.InternalException;

import java.util.Optional;

public class SecurityContextHolder
{
	private static final ThreadLocal<SecurityContext> securityContextLocal = new ThreadLocal<>();

	public static void setContext(SecurityContext context)
	{
		securityContextLocal.set(context);
	}

	public static void clearContext()
	{
		securityContextLocal.set(null);
		securityContextLocal.remove();
	}

	public static boolean isPresent()
	{
		return (securityContextLocal.get() != null);
	}

	public static SecurityContext getContext()
	{
		SecurityContext context = securityContextLocal.get();

		if(context == null)
		{
			throw new InternalException("SecurityContext was requested but is Null");
		}

		return context;
	}

	public static Optional<SecurityContext> getContextOptional()
	{
		return Optional.ofNullable(securityContextLocal.get());
	}

}
