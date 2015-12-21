package com.nowellpoint.aws.model;

import java.util.Date;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.amazonaws.services.dynamodbv2.datamodeling.encryption.DoNotEncrypt;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAutoGeneratedKey;

@DynamoDBTable(tableName="Events")
public class Event {
	
	public enum EventStatus {
		NEW,
		COMPLETE,
		ERROR
	};

	@DynamoDBAutoGeneratedKey
	@DynamoDBHashKey(attributeName="Id")  
	private String id;
	
	@DynamoDBRangeKey(attributeName="OrganizationId")  
	private String organizationId;
	
	@DynamoDBAttribute(attributeName="EventStatus")  
	private String eventStatus;
	
	@DynamoDBAttribute(attributeName="EventDate")  
	private Date eventDate;
	
	@DynamoDBAttribute(attributeName="Type")  
	private String type;
	
	@DynamoDBAttribute(attributeName="UserId")  
	private String userId;
	
	@DynamoDBAttribute(attributeName="Payload")
	private String payload;
	
	@DynamoDBAttribute(attributeName="RecordCount")
	private Integer recordCount;
	
	@DynamoDBAttribute(attributeName="ExecutionTime")
	private Long executionTime;
	
	@DynamoDBAttribute(attributeName="ErrorMessage")
	private String errorMessage;
	
	public Event() {
		
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@DoNotEncrypt
	public String getEventStatus() {
		return eventStatus;
	}

	public void setEventStatus(String eventStatus) {
		this.eventStatus = eventStatus;
	}

	@DoNotEncrypt
	public Date getEventDate() {
		return eventDate;
	}

	public void setEventDate(Date eventDate) {
		this.eventDate = eventDate;
	}

	@DoNotEncrypt
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getOrganizationId() {
		return organizationId;
	}

	public void setOrganizationId(String organizationId) {
		this.organizationId = organizationId;
	}

	@DoNotEncrypt
	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getPayload() {
		return payload;
	}

	public void setPayload(String payload) {
		this.payload = payload;
	}

	@DoNotEncrypt
	public Integer getRecordCount() {
		return recordCount;
	}

	public void setRecordCount(Integer recordCount) {
		this.recordCount = recordCount;
	}

	@DoNotEncrypt
	public Long getExecutionTime() {
		return executionTime;
	}

	public void setExecutionTime(Long executionTime) {
		this.executionTime = executionTime;
	}

	@DoNotEncrypt
	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	
	public Event withEventStatus(EventStatus eventStatus) {
		setEventStatus(eventStatus.name());
		return this;
	}
	
	public Event withEventDate(Date eventDate) {
		setEventDate(eventDate);
		return this;
	}
	
	public Event withType(String type) {
		setType(type);
		return this;
	}
	
	public Event withOrganizationId(String organizationId) {
		setOrganizationId(organizationId);
		return this;
	}
	
	public Event withUserId(String userId) {
		setUserId(userId);
		return this;
	}
	
	public Event withPayload(String payload) {
		setPayload(payload);
		return this;
	}
}