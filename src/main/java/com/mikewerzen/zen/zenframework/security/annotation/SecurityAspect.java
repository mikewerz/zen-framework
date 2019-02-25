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

package com.mikewerzen.zen.zenframework.security.annotation;

import com.mikewerzen.zen.zenframework.security.utils.SecurityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class SecurityAspect
{
	private static final Logger logger = LogManager.getLogger(SecurityAspect.class);

	@Autowired
	private SecurityUtils securityUtils;

	@Pointcut("execution(public * *(..))")
	void anyPublicMethod()
	{
	}

	@Around("anyPublicMethod() && @annotation(hasAllRoles)")
	public Object hasAllRoles(ProceedingJoinPoint proceedingJoinPoint, HasAllRoles hasAllRoles) throws Throwable
	{
		securityUtils.validateUserHasAllRoles(hasAllRoles.requiredRoles());
		return proceedingJoinPoint.proceed();
	}

	@Around("anyPublicMethod() && @annotation(hasRole)")
	public Object hasRole(ProceedingJoinPoint proceedingJoinPoint, HasRole hasRole) throws Throwable
	{
		securityUtils.validateUserHasRole(hasRole.requiredRole());
		return proceedingJoinPoint.proceed();
	}

	@Around("anyPublicMethod() && @annotation(hasAnyRole)")
	public Object hasAnyRole(ProceedingJoinPoint proceedingJoinPoint, HasAnyRole hasAnyRole) throws Throwable
	{
		securityUtils.validateUserHasAnyRole(hasAnyRole.authorizedRoles());
		return proceedingJoinPoint.proceed();
	}

	@Around("anyPublicMethod() && @annotation(hasAllEvents)")
	public Object hasAllEvents(ProceedingJoinPoint proceedingJoinPoint, HasAllEvents hasAllEvents) throws Throwable
	{
		securityUtils.validateUserHasAllEvents(hasAllEvents.requiredEvents());
		return proceedingJoinPoint.proceed();
	}

	@Around("anyPublicMethod() && @annotation(hasEvent)")
	public Object hasEvent(ProceedingJoinPoint proceedingJoinPoint, HasEvent hasEvent) throws Throwable
	{
		securityUtils.validateUserHasEvent(hasEvent.requiredEvent());
		return proceedingJoinPoint.proceed();
	}

	@Around("anyPublicMethod() && @annotation(hasAnyEvent)")
	public Object hasAnyEvent(ProceedingJoinPoint proceedingJoinPoint, HasAnyEvent hasAnyEvent) throws Throwable
	{
		securityUtils.validateUserHasAnyEvent(hasAnyEvent.authorizedEvents());
		return proceedingJoinPoint.proceed();
	}

	@Around("anyPublicMethod() && @annotation(isSignedIn)")
	public Object isSignedIn(ProceedingJoinPoint proceedingJoinPoint, IsSignedIn isSignedIn) throws Throwable
	{
		securityUtils.validateUserIsSignedIn();
		return proceedingJoinPoint.proceed();
	}
}
