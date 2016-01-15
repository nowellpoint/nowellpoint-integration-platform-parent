package com.nowellpoint.aws.model;

import java.net.URI;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class EventBuilder {
	
	private static ObjectMapper mapper = new ObjectMapper();
	private Class<?> type;
	private String accountId;
	private String eventSource;
	private EventAction eventAction;
	private Object payload;
	
	public EventBuilder withType(Class<?> type) {
		System.out.println("type: " + type.getName());
		this.type = type;
		return this;
	}
	
	public EventBuilder withAccountId(String accountId) {
		this.accountId = accountId;
		return this;
	}
	
	public EventBuilder withEventSource(URI eventSource) {
		this.eventSource = eventSource.toString();
		return this;
	}
	
	public EventBuilder withEventSource(String eventSource) {
		this.eventSource = eventSource;
		return this;
	}
	
	public EventBuilder withEventAction(EventAction eventAction) {
		this.eventAction = eventAction;
		return this;
	}
	
	public EventBuilder withPayload(Object payload) {
		this.payload = payload;
		return this;
	}
	
	public Event build() throws JsonProcessingException {
		
		if (type == null) {
			throw new IllegalArgumentException("Missing Type value");
		}
		
		if (accountId == null) {
			throw new IllegalArgumentException("Missing Account Id value");
		}
		
		if (eventSource == null) {
			throw new IllegalArgumentException("Missing EventSource value");
		}
		
		if (eventAction == null) {
			throw new IllegalArgumentException("Missing EventAction value");
		}
		
		if (payload == null) {
			throw new IllegalArgumentException("Missing Payload value");
		}
		
		return new Event(
				type.getName(),
				accountId,
				eventSource,
				eventAction,
				mapper.writeValueAsString(payload)
		);
	}
}