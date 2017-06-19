package com.nowellpoint.api.rest.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import javax.validation.ValidationException;

import org.jboss.logging.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.nowellpoint.api.service.CommunicationService;
import com.nowellpoint.api.util.MessageConstants;
import com.nowellpoint.api.util.MessageProvider;
import com.nowellpoint.http.HttpResponse;
import com.nowellpoint.http.MediaType;
import com.nowellpoint.http.RestResource;
import com.nowellpoint.http.Status;
import com.nowellpoint.util.Assert;

public class SlackCommunicationService implements CommunicationService {
	
	private static final Logger LOGGER = Logger.getLogger(SlackCommunicationService.class);

	@Override
	public void sendMessage(String webhookUrl, String username, String message)  {
		
		List<String> errors = new ArrayList<>();
		
		if (Assert.isNull(webhookUrl)) {
			errors.add(MessageProvider.getMessage(Locale.US, MessageConstants.SLACK_MISSING_WEBHOOK_URL));
		}
		
		if (Assert.isNull(username)) {
			errors.add(MessageProvider.getMessage(Locale.US, MessageConstants.SLACK_MISSING_USERNAME));
		}
		
		if (Assert.isNull(message)) {
			errors.add(MessageProvider.getMessage(Locale.US, MessageConstants.SLACK_MISSING_MESSAGE_TEXT));
		}
		
		if (! errors.isEmpty()) {
			String errorMessage = errors.stream().collect(Collectors.joining ("\n"));
			throw new ValidationException(errorMessage);
		}
		
		ObjectNode payload = new ObjectMapper().createObjectNode()
				.put("text", message)
				.put("icon_url", "https://www.nowellpoint.com/images/nowellpoint.jpg")
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