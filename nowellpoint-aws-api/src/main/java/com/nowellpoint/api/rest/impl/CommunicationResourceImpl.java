package com.nowellpoint.api.rest.impl;

import javax.inject.Inject;
import javax.ws.rs.core.Response;

import com.nowellpoint.api.rest.CommunicationResource;
import com.nowellpoint.api.service.CommunicationService;

public class CommunicationResourceImpl implements CommunicationResource {
	
	@Inject
	private CommunicationService communicationService;

	@Override
	public Response invokeAction(String action, String webhookUrl) {
		if ("test".equals(action)) {
			communicationService.sendMessage(webhookUrl, "Nowellpoint Notification Service", "Test to ensure external communication service functions as expected");
		}
		return Response.ok().build();
	}
}