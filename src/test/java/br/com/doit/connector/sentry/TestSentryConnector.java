package br.com.doit.connector.sentry;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.mock;

import java.util.HashMap;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import static org.mockito.Mockito.when;

import io.sentry.SentryClient;
import io.sentry.SentryClientFactory;
import io.sentry.dsn.Dsn;

public class TestSentryConnector {
	private static final String DSN = "https://key:value@sentry.io/123";

	@Rule
	public final ExpectedException thrown = ExpectedException.none();

	@Mock
	private SentryConnector connector;

	private SentryClient client;

	ConnectionManagementStrategy strategy;

	@Before
	public void setup() {
		SentryClientFactory factory = mock(SentryClientFactory.class);

		client = mock(SentryClient.class);

		when(factory.createSentryClient(new Dsn(DSN))).thenReturn(client);

		strategy = new ConnectionManagementStrategy(factory);

		connector = new SentryConnector();
		connector.setStrategy(strategy);
	}

	@Test
	public void sendExceptionToSentryWithValidSentryDNS() throws Exception {
		strategy.connect(DSN);

		Exception e = new Exception("Exception");

		connector.sendException(e, null);

		verify(strategy.client).sendException(e);
	}

	@Test
	public void sendExceptionAndExtraToSentryWithValidSentryDNSAndFilledExtraInfoHashMap() throws Exception {
		strategy.connect(DSN);

		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("1st", "First info");
		map.put("2nd", "Second info");

		Exception e = new Exception("Exception");

		connector.sendException(e, map);

		verify(client).sendException(e);
		verify(client).addExtra("1st", "First info");
		verify(client).addExtra("2nd", "Second info");
	}

}
