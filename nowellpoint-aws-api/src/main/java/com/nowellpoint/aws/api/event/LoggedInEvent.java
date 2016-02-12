package com.nowellpoint.aws.api.event;

import java.net.URI;

import com.nowellpoint.aws.idp.model.Token;

public class LoggedInEvent {

	private URI eventSource;
	
	private Token token;
	
	public LoggedInEvent() {
		
	}
	
	public LoggedInEvent(Token token, URI eventSource) {
		this.eventSource = eventSource;
		this.token = token;
	}

	public URI getEventSource() {
		return eventSource;
	}

	public void setEventSource(URI eventSource) {
		this.eventSource = eventSource;
	}

	public Token getToken() {
		return token;
	}

	public void setToken(Token token) {
		this.token = token;
	}
}