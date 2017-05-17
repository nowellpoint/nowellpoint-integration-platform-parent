package com.nowellpoint.api.rest.service;

import org.jboss.logging.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.nowellpoint.api.service.CommunicationService;
import com.nowellpoint.http.HttpResponse;
import com.nowellpoint.http.MediaType;
import com.nowellpoint.http.RestResource;
import com.nowellpoint.http.Status;

public class SlackCommunicationService implements CommunicationService {
	
	private static final Logger LOGGER = Logger.getLogger(SlackCommunicationService.class);

	@Override
	public void sendMessage(String webhookUrl, String username, String message)  {
		System.out.println(webhookUrl);
		ObjectNode payload = new ObjectMapper().createObjectNode()
				.put("text", message)
				.put("username", username);
		
		HttpResponse httpResponse = RestResource.post(webhookUrl)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.body(payload)
				.execute();	
		
		if (httpResponse.getStatusCode() != Status.OK) {
			LOGGER.error(httpResponse.getAsString());
		}			
	}
}