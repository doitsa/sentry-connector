package br.com.doit.connector.sentry;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import br.com.doit.connector.sentry.ConnectionManagementStrategy;
import io.sentry.SentryClient;
import io.sentry.SentryClientFactory;
import io.sentry.dsn.Dsn;
import io.sentry.dsn.InvalidDsnException;

public class TestConnectionManagementStrategy {
	private static final String DSN = "https://key:value@sentry.io/123";

	@Rule
	public final ExpectedException thrown = ExpectedException.none();

	public ConnectionManagementStrategy strategy;

	private SentryClient client;
	
	@Before
	public void setup() {
		SentryClientFactory factory = mock(SentryClientFactory.class);

		client = mock(SentryClient.class);

		when(factory.createSentryClient(new Dsn(DSN))).thenReturn(client);

		strategy = new ConnectionManagementStrategy(factory);
	}
	
	@Test
	public void isNotConnectedBeforeConnectingForTheFirstTime() throws Exception {
		assertThat(strategy.isConnected(), is(false));
	}

	@Test
	public void isConnectedWhenConnectingSuccessfully() throws Exception {
		strategy.connect(DSN);
		assertThat(strategy.isConnected(), is(true));
	}
	
	@Test
	public void sendExceptionToSentryWithInvalidSentryDNS() throws Exception {
		thrown.expect(InvalidDsnException.class);
		thrown.expectMessage("Invalid DSN, the following properties aren't set '[host]'");

		strategy.connect("123");
	}
}
