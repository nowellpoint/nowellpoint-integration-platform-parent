package com.nowellpoint.api.service;

public interface CommunicationService {

	public void sendMessage(String webhookUrl, String username, String message);
}