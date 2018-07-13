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
	 * Connect
	 *
	 * @param dsn
	 *            DSN (Data Source Name) of a chosen Sentry project. Use a DNS
	 *            that looks like
	 *            '{PROTOCOL}://{PUBLIC_KEY}@{HOST}/{PATH}{PROJECT_ID}'
	 * @throws ConnectionException
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
	 * Desconnect client
	 */
	@Disconnect
	public void disconnect() {
		client = null;
	}

	/**
	 * Are we connected?
	 */
	@ValidateConnection
	public boolean isConnected() {
		return client != null;
	}

	/**
	 * Connection identifier
	 */
	@ConnectionIdentifier
	public String connectionId() {
		return client.toString();
	}

}