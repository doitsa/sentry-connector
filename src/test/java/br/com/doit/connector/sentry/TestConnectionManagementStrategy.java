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
