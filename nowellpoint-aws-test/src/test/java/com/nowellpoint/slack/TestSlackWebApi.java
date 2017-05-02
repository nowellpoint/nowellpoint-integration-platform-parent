package com.nowellpoint.slack;

import org.junit.Test;

import com.nowellpoint.http.HttpResponse;
import com.nowellpoint.http.MediaType;
import com.nowellpoint.http.RestResource;

import static org.junit.Assert.assertEquals;

public class TestSlackWebApi {
	
	@Test
	public void testSendMessage() {
		
		Payload payload = new Payload();
		payload.setText("Sending a test message");
		payload.setUsername("Nowellpoint");
		payload.setIconUrl("https://www.nowellpoint.com/images/nowellpoint.jpg");
		
		HttpResponse httpResponse = RestResource.post("https://hooks.slack.com/services/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")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.body(payload)
				.execute();
		
		System.out.println(httpResponse.getStatusCode());
		System.out.println(httpResponse.getAsString());
		
		assertEquals(200, httpResponse.getStatusCode());
	}
}