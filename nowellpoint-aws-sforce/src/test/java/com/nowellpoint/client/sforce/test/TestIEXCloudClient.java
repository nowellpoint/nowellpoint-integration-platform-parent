package com.nowellpoint.client.sforce.test;

import org.junit.Test;

import pl.zankowski.iextrading4j.api.stocks.Quote;
import pl.zankowski.iextrading4j.client.IEXCloudClient;
import pl.zankowski.iextrading4j.client.IEXCloudTokenBuilder;
import pl.zankowski.iextrading4j.client.IEXTradingApiVersion;
import pl.zankowski.iextrading4j.client.IEXTradingClient;
import pl.zankowski.iextrading4j.client.rest.request.stocks.QuoteRequestBuilder;

public class TestIEXCloudClient {
	
	@Test
	public void testGetQuote() {
		
		final IEXCloudClient iexTradingClient = IEXTradingClient.create(IEXTradingApiVersion.IEX_CLOUD_V1, 
				new IEXCloudTokenBuilder()
				.withPublishableToken("")
	                      .withSecretToken("")
	                      .build());
		
		final Quote quote = iexTradingClient.executeRequest(new QuoteRequestBuilder()
				.withSymbol("AAPL")
				.build());
		
		System.out.println(quote);
	}
}