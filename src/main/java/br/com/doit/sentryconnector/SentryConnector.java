package br.com.doit.sentryconnector;

import java.util.HashMap;
import java.util.Map.Entry;

import org.mule.api.ConnectionException;
import org.mule.api.annotations.Connect;
import org.mule.api.annotations.ConnectionIdentifier;
import org.mule.api.annotations.Connector;
import org.mule.api.annotations.Disconnect;
import org.mule.api.annotations.Processor;
import org.mule.api.annotations.ValidateConnection;
import org.mule.api.annotations.param.ConnectionKey;

import io.sentry.DefaultSentryClientFactory;
import io.sentry.SentryClient;
import io.sentry.SentryClientFactory;
import io.sentry.dsn.Dsn;
import io.sentry.dsn.InvalidDsnException;

/**
 * Sentry Connector.
 *
 * @author Luiz Alfredo
 */
@Connector(name = "sentry", schemaVersion = "1.0", friendlyName = "Sentry Connector")
public class SentryConnector {

	private SentryClient client;

	private final SentryClientFactory factory;

	SentryConnector(SentryClientFactory factory) {
		this.factory = factory;
	}

	public SentryConnector() {
		this.factory = new DefaultSentryClientFactory();
	}

	/**
	 * Connect
	 *
	 * @param dsn
	 *            Dsn of a chosen Sentry project.
	 * @throws ConnectionException
	 */
	@Connect
	public void connect(@ConnectionKey String dsn) throws ConnectionException  {
		try {
			client = factory.createSentryClient(new Dsn(dsn));
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

	/**
	 * Send an exception to Sentry according to the informed dsn.
	 *
	 * {@sample.xml ../../../doc/sentry-connector.xml.sample
	 * sentry:send-exception}
	 * 
	 * @param exception
	 *            A Java Lang Exception
	 *
	 * @param extraInfoMap
	 *            A map of containing extra info (Optional)
	 */
	@Processor(friendlyName = "Send exception")
	public void sendException(Exception exception, HashMap<String, Object> extraInfoMap) {
		
		if(extraInfoMap != null){
			for (Entry<String, Object> entry : extraInfoMap.entrySet()) {
				client.addExtra(entry.getKey(), entry.getValue());
			}
		}

		client.sendException(exception);
	}
	
}
