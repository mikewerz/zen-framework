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

package com.mikewerzen.zen.zenframework.util;

import org.springframework.core.convert.converter.Converter;

import javax.swing.text.DateFormatter;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;

public class TimeUtils
{

	public static ZonedDateTime getEasternTime()
	{
		return ZonedDateTime.of(LocalDateTime.now(), TimeZone.getTimeZone("EST").toZoneId());
	}

	public static ZonedDateTime getEasternTime(int hour, int minutes)
	{
		return getEasternTime().withHour(hour).withMinute(minutes);
	}

	public static ZonedDateTime getEasternTime(int hour, int minutes, boolean isPM)
	{
		if(isPM && hour < 12)
			hour += 12;

		return getEasternTime().withHour(hour).withMinute(minutes);
	}

	public static ZonedDateTime getEasternTime(int year, int month, int day, int hour, int minutes)
	{
		return getEasternTime().withYear(year).withMonth(month).withDayOfMonth(day).withHour(hour).withMinute(minutes);
	}

	public static ZonedDateTime getEasternTime(int year, int month, int day, int hour, int minutes, boolean isPM)
	{
		if(isPM && hour < 12)
			hour += 12;

		return getEasternTime().withYear(year).withMonth(month).withDayOfMonth(day).withHour(hour).withMinute(minutes);
	}

	public static ZonedDateTime updateDateWithTime(ZonedDateTime date, LocalTime time)
	{
		return date.withHour(time.getHour()).withMinute(time.getMinute()).withSecond(time.getSecond());
	}

	public static Converter<String, ZonedDateTime> getZonedDateTimeConverter()
	{
		return new Converter<String, ZonedDateTime>()
		{
			private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

			@Override public ZonedDateTime convert(String source)
			{
				if(source == null || source.isEmpty())
				{
					return null;
				}

				LocalDate date = LocalDate.parse(source, formatter);
				return date.atStartOfDay(TimeUtils.getEasternTime().getZone());
			}
		};
	}


}
