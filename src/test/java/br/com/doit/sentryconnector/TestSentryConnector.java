package br.com.doit.sentryconnector;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;

import br.com.doit.sentryconnector.SentryConnector;
import io.sentry.SentryClient;
import io.sentry.SentryClientFactory;
import io.sentry.dsn.Dsn;
import io.sentry.dsn.InvalidDsnException;

public class TestSentryConnector {

	private static final String DSN = "https://key:value@sentry.io/123";

	public SentryConnector connector;

	private SentryClient client;

	@Before
	public void setup() {
		SentryClientFactory factory = mock(SentryClientFactory.class);

		client = mock(SentryClient.class);

		when(factory.createSentryClient(new Dsn(DSN))).thenReturn(client);

		connector = new SentryConnector(factory);
	}

	@Test
	public void isNotConnectedBeforeConnectingForTheFirstTime() throws Exception {
		assertThat(connector.isConnected(), is(false));
	}

	@Test
	public void isConnectedWhenConnectingSuccessfully() throws Exception {
		connector.connect(DSN);
		assertThat(connector.isConnected(), is(true));
	}

	@Test
	public void sendExceptionToSentryWithValidSentryDNS() throws Exception {
		connector.connect(DSN);

		Exception e = new Exception("Exception");
		
		connector.sendException(e, null);

		verify(client).sendException(e);
	}
	
	@Test
	public void sendExceptionAndExtraToSentryWithValidSentryDNSAndFilledExtraInfoHashMap() throws Exception {
		connector.connect(DSN);

		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("1st", "First info");
		map.put("2nd", "Second info");
		
		Exception e = new Exception("Exception");
		
		connector.sendException(e, map);

		verify(client).sendException(e);
		verify(client).addExtra("1st", "First info");
		verify(client).addExtra("2nd", "Second info");
	}
	
	@Test
	public void sendExceptionToSentryWithEmptySentryDNS() throws Exception {
		try{
			connector.connect("");
		}catch (InvalidDsnException exception){
			assertThat(exception.getMessage(), is("Invalid DSN, the following properties aren't set '[host]'"));
		}
	}
	
	@Test
	public void sendExceptionToSentryWithInvalidSentryDNS() throws Exception {
		try{
			connector.connect("https://value@sentry.io/123");
		}catch (InvalidDsnException exception){
			assertThat(exception.getMessage(), is("Invalid DSN, the following properties aren't set '[secret key]'"));
		}
	}

}
