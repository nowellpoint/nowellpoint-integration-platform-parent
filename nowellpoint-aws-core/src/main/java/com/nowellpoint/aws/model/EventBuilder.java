package com.nowellpoint.aws.model;

import java.net.URI;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class EventBuilder {
	
	private static ObjectMapper mapper = new ObjectMapper();
	private String organizationId;
	private Class<?> type;
	private String accountId;
	private String eventSource;
	private EventAction eventAction;
	private String configurationId;
	private Object payload;
	
	public EventBuilder withOrganizationId(String organizationId) {
		this.organizationId = organizationId;
		return this;
	}
	
	public EventBuilder withType(Class<?> type) {
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
	
	public EventBuilder withConfigurationId(String configurationId) {
		this.configurationId = configurationId;
		return this;
	}
	
	public EventBuilder withPayload(Object payload) {
		this.payload = payload;
		return this;
	}
	
	public Event build() throws JsonProcessingException {
		
		if (organizationId == null) {
			throw new IllegalArgumentException("Missing Organization Id value");
		}
		
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
		
		if (configurationId == null) {
			throw new IllegalArgumentException("Missing ConfigurationId value");
		}
		
		if (payload == null) {
			throw new IllegalArgumentException("Missing Payload value");
		}
		
		return new Event(
				organizationId, 
				type,
				accountId,
				eventSource,
				eventAction,
				configurationId,
				mapper.writeValueAsString(payload)
		);
	}
}