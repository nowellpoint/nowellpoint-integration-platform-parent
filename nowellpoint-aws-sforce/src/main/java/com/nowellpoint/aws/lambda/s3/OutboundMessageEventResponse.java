package com.nowellpoint.aws.lambda.s3;

import org.bson.types.ObjectId;

public class OutboundMessageEventResponse {
	
	public enum Status {
		SUCCESS,
		FAIL
	}
	
	public enum Action {
		CREATE,
		UPDATE,
		DELETE
	}
	
	private Status status;
	
	private Action action;
	
	private String collection;
	
	private ObjectId objectId;
	
	private Long executionTime;
	
	private String exceptionMessage;
	
	public OutboundMessageEventResponse() {
		
	}
	
	public OutboundMessageEventResponse withStatus(Status status) {
		this.status = status;
		return this;
	}
	
	public OutboundMessageEventResponse withAction(Action action) {
		this.action = action;
		return this;
	}
	
	public OutboundMessageEventResponse withCollection(String collection) {
		this.collection = collection;
		return this;
	}
	
	public OutboundMessageEventResponse withObjectId(ObjectId objectId) {
		this.objectId = objectId;
		return this;
	}
	
	public OutboundMessageEventResponse withExecutionTime(Long executionTime) {
		this.executionTime = executionTime;
		return this;
	}
	
	public OutboundMessageEventResponse withExceptionMessage(String exceptionMessage) {
		this.exceptionMessage = exceptionMessage;
		return this;
	}

	public Status getStatus() {
		return status;
	}
	
	public Action getAction() {
		return action;
	}
	
	public String getCollection() {
		return collection;
	}
	
	public ObjectId getObjectId() {
		return objectId;
	}
	
	public Long getExecutionTime() {
		return executionTime;
	}
	
	public String getExceptionMessage() {
		return exceptionMessage;		
	}
}