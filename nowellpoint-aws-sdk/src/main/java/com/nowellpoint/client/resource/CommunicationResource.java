package com.nowellpoint.client.resource;

import com.nowellpoint.client.model.Token;
import com.nowellpoint.http.HttpResponse;
import com.nowellpoint.http.MediaType;
import com.nowellpoint.http.RestResource;
import com.nowellpoint.http.Status;

public class CommunicationResource extends AbstractResource {
	
	private static final String RESOURCE_CONTEXT = "communications";

	public CommunicationResource(Token token) {
		super(token);
	}
	
	public void test(String webhookUrl) {
		HttpResponse httpResponse = RestResource.post(token.getEnvironmentUrl())
				.bearerAuthorization(token.getAccessToken())
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.path(RESOURCE_CONTEXT)
    			.path("slack")
    			.path("actions")
    			.path("test")
    			.path("invoke")
    			.parameter("webhookUrl", webhookUrl)
    			.execute();
		
		if (httpResponse.getStatusCode() == Status.OK) {
			
		}
	}

}