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

package com.mikewerzen.zen.zenframework.logging.export;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

@Component
public class SplunkDataExporter implements LogDataExporter
{
	private static final Logger logger = LogManager.getLogger(SplunkDataExporter.class);

	@Value("${log.export.splunk.url}")
	private String splunkUrl = null;

	@Value("${log.export.splunk.token}")
	private String token = null;

	@Value("${log.export.splunk.index}")
	private String index = null;

	@Value("${log.export.splunk.source}")
	private String source = null;

	@Value("${log.export.splunk.sourcetype}")
	private String sourcetype = null;

	@Value("${log.export.splunk.host}")
	private String host = null;

	private RestTemplate restTemplate = buildRestTemplate();

	@Override
	@Async
	public void exportLogMessage(String logMessage)
	{
		try
		{
			RequestEntity<String> requestEntity = new RequestEntity<String>(buildBody(logMessage), buildHeaders(), HttpMethod.POST, URI.create(splunkUrl));

			logger.debug("Exporting log message to Splunk");
			ResponseEntity<String> response = restTemplate.exchange(requestEntity, String.class);
		}
		catch (Exception e)
		{
			logger.error("Could not export log data to Splunk!", e);
		}

	}

	private MultiValueMap<String, String> buildHeaders()
	{
		MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
		headers.add("Authorization", "Splunk " + token);
		return headers;
	}

	private String buildBody(String logMessage)
	{
		return new StringBuilder()
				.append("{")
				.append("\"index\": ").append("\"").append(index).append("\",")
				.append("\"source\": ").append("\"").append(source).append("\",")
				.append("\"sourcetype\": ").append("\"").append(sourcetype).append("\",")
				.append("\"host\": ").append("\"").append(host).append("\",")
				.append("\"event\": ").append(logMessage)
				.append("}")
				.toString();
	}

	private RestTemplate buildRestTemplate()
	{
		return new RestTemplateBuilder()
				.setConnectTimeout(15000)
				.setReadTimeout(15000)
				.build();
	}
}
