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

import org.mule.api.ConnectionException;
import org.mule.api.annotations.Connect;
import org.mule.api.annotations.ConnectionIdentifier;
import org.mule.api.annotations.Disconnect;
import org.mule.api.annotations.TestConnectivity;
import org.mule.api.annotations.ValidateConnection;
import org.mule.api.annotations.components.ConnectionManagement;
import org.mule.api.annotations.display.FriendlyName;
import org.mule.api.annotations.param.ConnectionKey;

import io.sentry.DefaultSentryClientFactory;
import io.sentry.Sentry;
import io.sentry.SentryClient;
import io.sentry.SentryClientFactory;
import io.sentry.dsn.InvalidDsnException;

@ConnectionManagement(friendlyName = "Connector Connection")
public class ConnectionManagementStrategy {

	public SentryClient client;

	private final SentryClientFactory factory;

	public ConnectionManagementStrategy(SentryClientFactory factory) {
		this.factory = factory;
	}

	public ConnectionManagementStrategy() {
		this.factory = new DefaultSentryClientFactory();
	}

	/**
	 * Connect to Sentry using the provided DSN.
	 *
	 * @param dsn
	 *            DSN (Data Source Name) of a chosen Sentry project. Use a DSN
	 *            that looks like
	 *            '{PROTOCOL}://{PUBLIC_KEY}@{HOST}/{PATH}{PROJECT_ID}'
	 * @throws ConnectionException
	 *             if cannot connect to Sentry using the provided DSN.
	 */
	@TestConnectivity
	@Connect
	public void connect(@ConnectionKey String dsn) throws ConnectionException {
		try {
			client = SentryClientFactory.sentryClient(dsn, factory);
		} catch (InvalidDsnException exception) {
			throw exception;
		}
	}

	/**
	 * Disconnect client
	 */
	@Disconnect
	public void disconnect() {
		client = null;
	}

	/**
	 * Check if this connector is connected to Sentry.
	 * 
	 * @return Return {@code true} if it's still connected or {@code false}
	 *         otherwise.
	 */
	@ValidateConnection
	public boolean isConnected() {
		return client != null;
	}

	/**
	 * The connection identifier.
	 * 
	 * @return Returns the connection identifier.
	 */
	@ConnectionIdentifier
	public String connectionId() {
		return client.toString();
	}

}