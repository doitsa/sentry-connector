/**
 * Copyright (c) 2018 DOit SA
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package br.com.doit.connector.sentry;

import java.util.HashMap;
import java.util.Map.Entry;

import org.mule.api.annotations.Config;
import org.mule.api.annotations.Connector;
import org.mule.api.annotations.Processor;
import org.mule.api.annotations.param.Optional;

import io.sentry.SentryClient;

/**
 * Sentry is an open-source error tracking that helps developers monitor and fix
 * crashes in real time. This connector sends exception data using the Sentry
 * SDK.
 * 
 * @author Luiz Alfredo
 */
@Connector(name = "sentry", friendlyName = "Sentry Connector")
public class SentryConnector {

	@Config
	ConnectionManagementStrategy strategy;

	public ConnectionManagementStrategy getStrategy() {
		return strategy;
	}

	public void setStrategy(ConnectionManagementStrategy strategy) {
		this.strategy = strategy;
	}

	/**
	 * Send an exception to Sentry according to the informed DSN.
	 *
	 * {@sample.xml ../../../doc/sentry-connector.xml.sample
	 * sentry:send-exception}
	 * 
	 * @param exception
	 *            A Java Lang Exception
	 *
	 * @param extraInfoMap
	 *            A map of extra info (Optional)
	 */
	@Processor(friendlyName = "Send exception")
	public void sendException(Exception exception, @Optional HashMap<String, Object> extraInfoMap) {
		SentryClient client = strategy.client;

		if (extraInfoMap != null) {
			for (Entry<String, Object> entry : extraInfoMap.entrySet()) {
				client.addExtra(entry.getKey(), entry.getValue());
			}
		}

		client.sendException(exception);
	}
}