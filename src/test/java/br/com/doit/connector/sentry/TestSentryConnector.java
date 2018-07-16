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
