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

package com.mikewerzen.zen.zenframework.security.utils;

import com.mikewerzen.zen.zenframework.exception.logic.InsufficientAuthorizationException;
import com.mikewerzen.zen.zenframework.exception.system.InternalException;
import com.mikewerzen.zen.zenframework.security.context.SecurityContextHolder;
import com.mikewerzen.zen.zenframework.security.token.SecurityToken;
import com.mikewerzen.zen.zenframework.validation.InternalValidationUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


import java.util.Arrays;
import java.util.List;

@Component
public class SecurityUtils
{
	private static final InternalValidationUtils validationUtils = InternalValidationUtils.getInstance();

	@Value("${security.zen.iam.app.name}")
	private String appName;

	SecurityUtils()
	{

	}

	public SecurityUtils(String appName)
	{
		validationUtils.isNotTrimmedEmpty("AppName", appName);
		this.appName = appName;
	}

	public static boolean isUserSameAsUserInContext(String appName, String username)
	{
		validateUserIsSignedIn();
		return appName.equals(getThreadSecurityToken().getAppName())
				&& username.equals(getThreadSecurityToken().getUsername());
	}

	public static boolean isUserSameAsUserInContext(String userId)
	{
		validateUserIsSignedIn();
		return userId.equals(getThreadSecurityToken().getUserId());
	}

	public static void validateUserIsSignedIn()
	{
		validateSecurityContextHasCredentials();
	}

	public void validateUserIsSignedIntoApp()
	{
		validateSecurityContextHasCredentials();
		validateAppIsSameAsContext();
	}

	public static void validateUserHasUserId(String userId)
	{
		validateUserIsSignedIn();

		try
		{
			validationUtils.isEqualTo("UserId", userId, getThreadSecurityToken().getUserId());
		}
		catch (InternalException exception)
		{
			throw new InsufficientAuthorizationException("User is signed in as a different user.", exception);
		}
	}

	public void validateUserHasUsername(String username)
	{
		validateUserIsSignedIntoApp();

		validationUtils.isNotTrimmedEmpty("username", username);

		try
		{
			validationUtils.isEqualTo("Username", username, getThreadSecurityToken().getUsername());
		}
		catch (InternalException exception)
		{
			throw new InsufficientAuthorizationException("User is signed in as a different user.", exception);
		}
	}

	public static void validateUserHasRole(String roleName)
	{
		validateUserIsSignedIn();

		validateUserHasAnyRole(new String[]{roleName});
	}

	public static void validateUserHasAnyRole(String[] roles)
	{
		validateUserHasAnyRole(Arrays.asList(roles));
	}

	public static void validateUserHasAnyRole(List<String> roles)
	{
		validateUserIsSignedIn();
		validateUserHasSecurityRoles();

		try
		{
			validationUtils.isAnyValueInCollection("Roles", Arrays.asList(getThreadSecurityToken().getRoles()), roles);
		}
		catch (InternalException exception)
		{
			throw new InsufficientAuthorizationException("User is not authorized for any of the necessary roles.",
					exception);
		}
	}

	private static void validateUserHasSecurityRoles()
	{
		if (getThreadSecurityToken() == null || getThreadSecurityToken().getRoles() == null || getThreadSecurityToken
				().getRoles().length == 0)
		{
			throw new InsufficientAuthorizationException("User is not authorized for any of the necessary roles.");
		}
	}

	private static void validateUserHasSecurityEvents()
	{
		if (getThreadSecurityToken() == null || getThreadSecurityToken().getEvents() == null ||
				getThreadSecurityToken()
				.getEvents().length == 0)
		{
			throw new InsufficientAuthorizationException("User is not authorized for any of the necessary roles.");
		}
	}

	public static void validateUserHasAllRoles(String[] validRoles)
	{
		validateUserHasAllRoles(Arrays.asList(validRoles));
	}

	public static void validateUserHasAllRoles(List<String> validRoles)
	{
		validateUserIsSignedIn();
		validateUserHasSecurityRoles();

		try
		{
			validationUtils.areAllValuesInCollection("Roles",
					Arrays.asList(getThreadSecurityToken().getRoles()),
					validRoles);
		}
		catch (InternalException exception)
		{
			throw new InsufficientAuthorizationException("User is not authorized for all of the necessary roles.",
					exception);
		}
	}

	public static void validateUserHasEvent(String eventName)
	{
		validateUserIsSignedIn();

		validateUserHasAnyEvent(new String[]{eventName});
	}

	public static void validateUserHasAnyEvent(String[] events)
	{
		validateUserHasAnyEvent(Arrays.asList(events));
	}

	public static void validateUserHasAnyEvent(List<String> events)
	{
		validateUserIsSignedIn();
		validateUserHasSecurityEvents();

		try
		{
			validationUtils.isAnyValueInCollection("Roles",
					Arrays.asList(getThreadSecurityToken().getEvents()),
					events);
		}
		catch (InternalException exception)
		{
			throw new InsufficientAuthorizationException("User is not authorized for any of the necessary events.",
					exception);
		}
	}


	public static void validateUserHasAllEvents(String[] validEvents)
	{
		validateUserHasAllEvents(Arrays.asList(validEvents));
	}

	public static void validateUserHasAllEvents(List<String> validEvents)
	{
		validateUserIsSignedIn();
		validateUserHasSecurityEvents();

		try
		{
			validationUtils.areAllValuesInCollection("Roles",
					Arrays.asList(getThreadSecurityToken().getEvents()),
					validEvents);
		}
		catch (InternalException exception)
		{
			throw new InsufficientAuthorizationException("User is not authorized for all of the necessary events.",
					exception);
		}
	}

	public static void validateSecurityContextHasCredentials()
	{
		if (!SecurityContextHolder.getContextOptional().isPresent())
		{
			throw new InsufficientAuthorizationException("User is not signed in.");
		}
	}

	private void validateAppIsSameAsContext()
	{
		try
		{
			validationUtils.isEqualTo("AppName", appName, getThreadSecurityToken().getAppName());
		}
		catch (InternalException exception)
		{
			throw new InsufficientAuthorizationException("User is not signed into this application.", exception);
		}
	}

	public static SecurityToken getThreadSecurityToken()
	{
		return SecurityContextHolder.getContext().getSecurityToken();
	}
}
