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

package com.mikewerzen.zen.zenframework.logging;

import com.mikewerzen.zen.zenframework.logging.builder.LogMessageBuilder;
import com.mikewerzen.zen.zenframework.logging.export.LogDataExporter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ZenLogManager
{
	private static final Logger logger = LogManager.getLogger(ZenLogManager.class);

	@Value("${log.export.enabled:false}")
	private boolean exportEnabled = false;

	@Autowired
	private LogMessageBuilder logMessageBuilder;

	@Autowired
	private LogDataExporter logDataExporter;

	public void logTransaction(Object response)
	{
		writeLogMessage(logMessageBuilder.buildTransactionLog(response));
	}

	public void logEvent(Object response)
	{
		writeLogMessage(logMessageBuilder.buildEventLog(response));
	}

	private void writeLogMessage(String logMessage)
	{
		logger.info(logMessage);

		if (exportEnabled)
		{
			logDataExporter.exportLogMessage(logMessage);
		}
	}

}
