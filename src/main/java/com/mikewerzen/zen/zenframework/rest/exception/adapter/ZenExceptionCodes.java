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

package com.mikewerzen.zen.zenframework.rest.exception.adapter;

import org.springframework.http.HttpStatus;

/**
 * Class defines default exception codes used by the Zen Framework.
 *
 * Primarily, clients should rely on HttpStatus codes to determine the correct follow-up behavior.
 * Individual Zen Exception Codes are meant for usage in debugging and communicating error information
 * rather than for clients to take action upon.
 *
 * Zen Exception Code Ranges:
 * 		1xx -> Client ZenError
 * 		2xx -> Gateway Errors
 *		3xx -> Authentication/Authorization Errors
 * 		4xx -> Database ZenError
 * 		5xx -> External API ZenError
 * 		6xx -> Application ZenError
 */
public class ZenExceptionCodes
{
	public static final HttpStatus INVALID_REQUEST_HTTP_STATUS = HttpStatus.BAD_REQUEST;
	public static final long INVALID_REQUEST_ERROR_CODE = 100;
	public static final String INVALID_REQUEST_MESSAGE = "The request was invalid or cannot be served.";

	public static final HttpStatus NOT_FOUND_HTTP_STATUS = HttpStatus.NOT_FOUND;
	public static final long NOT_FOUND_CODE = 110;
	public static final String NOT_FOUND_MESSAGE = "The URI requested is invalid or the resource does not exist.";

	public static final HttpStatus NOT_IMPLEMENTED_HTTP_STATUS = HttpStatus.NOT_IMPLEMENTED;
	public static final long NOT_IMPLEMENTED_CODE = 210;
	public static final String NOT_IMPLEMENTED_MESSAGE = "The API endpoint has not yet been implemented.";

	public static final HttpStatus REMOVED_HTTP_STATUS = HttpStatus.GONE;
	public static final long REMOVED_CODE = 220;
	public static final String REMOVED_MESSAGE = "The API endpoint has been turned off. Please upgrade to a newer endpoint.";

	public static final HttpStatus AUTH_FAILURE_HTTP_STATUS = HttpStatus.UNAUTHORIZED;
	public static final long AUTH_FAILURE_CODE = 300;
	public static final String AUTH_FAILURE_MESSAGE = "Failed to authorize user.";

	public static final HttpStatus INVALID_AUTHENTICATION_HTTP_STATUS = HttpStatus.UNAUTHORIZED;
	public static final long INVALID_AUTHENTICATION_CODE = 310;
	public static final String INVALID_AUTHENTICATION_MESSAGE = "Missing or incorrect credentials.";

	public static final HttpStatus INSUFFICIENT_AUTHENTICATION_HTTP_STATUS = HttpStatus.FORBIDDEN;
	public static final long INSUFFICIENT_AUTHORIZATION_CODE = 320;
	public static final String INSUFFICIENT_AUTHORIZATION_MESSAGE = "The request was understood, but it has been refused.";

	public static final HttpStatus DATABASE_ERROR_HTTP_STATUS = HttpStatus.INTERNAL_SERVER_ERROR;
	public static final long DATABASE_ERROR_CODE = 400;
	public static final String DATABASE_ERROR_MESSAGE = "A database error occurred.";

	public static final HttpStatus EXTERNAL_ERROR_HTTP_STATUS = HttpStatus.BAD_GATEWAY;
	public static final long EXTERNAL_ERROR_CODE = 510;
	public static final String EXTERNAL_ERROR_MESSAGE = "An error occurred on an external server while processing your request.";

	public static final HttpStatus EXTERNAL_TIMEOUT_HTTP_STATUS = HttpStatus.GATEWAY_TIMEOUT;
	public static final long EXTERNAL_TIMEOUT_CODE = 530;
	public static final String EXTERNAL_TIMEOUT_MESSAGE = "A timeout occurred while connecting to an external server while processing your request.";

	public static final HttpStatus INTERNAL_ERROR_HTTP_STATUS = HttpStatus.INTERNAL_SERVER_ERROR;
	public static final long INTERNAL_ERROR_CODE = 610;
	public static final String INTERNAL_ERROR_MESSAGE = "An internal server error occurred.";

	public static final HttpStatus UNKNOWN_ERROR_HTTP_STATUS = HttpStatus.INTERNAL_SERVER_ERROR;
	public static final long UNKNOWN_ERROR_CODE = 690;
	public static final String UNKNOWN_ERROR_MESSAGE = "An unknown error occurred.";

}
