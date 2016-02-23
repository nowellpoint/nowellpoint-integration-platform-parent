package com.nowellpoint.aws.data.dynamodb;

import java.net.URI;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class EventBuilder {
	
	private static ObjectMapper mapper = new ObjectMapper();
	private Class<?> type;
	private String subject;
	private String eventSource;
	private EventAction eventAction;
	private Object payload;
	private String propertyStore;
	private String parentEventId;
	
	public EventBuilder withType(Class<?> type) {
		this.type = type;
		return this;
	}
	
	public EventBuilder withSubject(String subject) {
		this.subject = subject;
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
	
	public EventBuilder withPropertyStore(String propertyStore) {
		this.propertyStore = propertyStore;
		return this;
	}
	
	public EventBuilder withParentEventId(String parentEventId) {
		this.parentEventId = parentEventId;
		return this;
	}
	
	public Event build() throws JsonProcessingException {
		
		if (type == null) {
			throw new IllegalArgumentException("Missing Type value");
		}
		
		if (subject == null) {
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
		
		if (propertyStore == null) {
			throw new IllegalArgumentException("Missing Property Store value");
		}
		
		return new Event(
				type.getName(),
				subject,
				eventSource,
				eventAction,
				mapper.writeValueAsString(payload),
				propertyStore,
				parentEventId
		);
	}
}