package com.nowellpoint.aws.lambda.stream;

import org.bson.types.ObjectId;

public class TransactionEventResponse {
	
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
	
	public TransactionEventResponse() {
		
	}
	
	public TransactionEventResponse withStatus(Status status) {
		this.status = status;
		return this;
	}
	
	public TransactionEventResponse withAction(Action action) {
		this.action = action;
		return this;
	}
	
	public TransactionEventResponse withCollection(String collection) {
		this.collection = collection;
		return this;
	}
	
	public TransactionEventResponse withObjectId(ObjectId objectId) {
		this.objectId = objectId;
		return this;
	}
	
	public TransactionEventResponse withExecutionTime(Long executionTime) {
		this.executionTime = executionTime;
		return this;
	}
	
	public TransactionEventResponse withExceptionMessage(String exceptionMessage) {
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