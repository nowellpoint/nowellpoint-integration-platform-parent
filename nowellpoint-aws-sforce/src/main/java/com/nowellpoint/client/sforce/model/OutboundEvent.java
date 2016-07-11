package com.nowellpoint.client.sforce.model;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class OutboundEvent {
	
	@JsonProperty("attributes")
	private Attributes attributes;
	
	@JsonProperty("Event_Type__c")
	private String eventType;
	
	@JsonProperty("Id")
	private String id;
	
	@JsonProperty("Object__c")
	private String object;
	
	@JsonProperty("Object_Id__c")
	private String objectId;
	
	private Date createdDate;
	
	public OutboundEvent() {
		
	}

	public Attributes getAttributes() {
		return attributes;
	}

	public void setAttributes(Attributes attributes) {
		this.attributes = attributes;
	}

	public String getEventType() {
		return eventType;
	}

	public void setEventType(String eventType) {
		this.eventType = eventType;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getObject() {
		return object;
	}

	public void setObject(String object) {
		this.object = object;
	}

	public String getObjectId() {
		return objectId;
	}

	public void setObjectId(String objectId) {
		this.objectId = objectId;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}
}