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

package com.mikewerzen.zen.zenframework.validation;

import com.mikewerzen.zen.zenframework.exception.logic.ValidationException;

public class RequestValidationUtils extends AbstractValidationUtils
{

	private RequestValidationUtils()
	{

	}

	private static RequestValidationUtils instance = new RequestValidationUtils();

	public static RequestValidationUtils getInstance()
	{
		return instance;
	}


	@Override
	RuntimeException generateExceptionForMessage(String message)
	{
		return new ValidationException(message);
	}

	@Override
	RuntimeException generateExceptionForMessage(String message, Throwable cause)
	{
		return new ValidationException(message, cause);
	}
}
