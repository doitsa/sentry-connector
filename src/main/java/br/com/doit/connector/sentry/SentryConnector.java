package br.com.doit.connector.sentry;

import java.util.HashMap;
import java.util.Map.Entry;

import org.mule.api.annotations.Config;
import org.mule.api.annotations.Connector;
import org.mule.api.annotations.Processor;
import org.mule.api.annotations.param.Optional;

import io.sentry.SentryClient;

@Connector(name="sentry", friendlyName="Sentry Connector")
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
	public void sendException(Exception exception, @Optional HashMap<String, Object> extraInfoMap) {
		
		SentryClient client = strategy.client;
		
		if(extraInfoMap != null){
			for (Entry<String, Object> entry : extraInfoMap.entrySet()) {
				client.addExtra(entry.getKey(), entry.getValue());
			}
		}

		client.sendException(exception);
	}

}