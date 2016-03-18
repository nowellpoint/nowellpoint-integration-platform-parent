package com.nowellpoint.aws.api.event;

import java.net.URI;

public class LoggedInEvent {

	private URI eventSource;
	
	private String subject;
	
	public LoggedInEvent() {
		
	}
	
	public LoggedInEvent(URI eventSource, String subject) {
		this.eventSource = eventSource;
		this.subject = subject;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public URI getEventSource() {
		return eventSource;
	}

	public void setEventSource(URI eventSource) {
		this.eventSource = eventSource;
	}
}