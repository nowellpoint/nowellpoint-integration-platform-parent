package com.nowellpoint.aws.lambda.stream;

import org.bson.types.ObjectId;

import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.mongodb.client.MongoDatabase;
import com.nowellpoint.aws.model.sforce.Notification;

public class TransactionEventRequest {
	
	private LambdaLogger logger;
	
	private String partnerURL;
	
	private String sessionId;
	
	private MongoDatabase mongoDatabase;
	
	private ObjectId userId; 
	
	private ObjectId organizationId;
	
	private Notification notification;
	
	public TransactionEventRequest() {
		
	}
	
	public TransactionEventRequest withLogger(LambdaLogger logger) {
		this.logger = logger;
		return this;
	}
	
	public TransactionEventRequest withPartnerURL(String partnerURL) {
		this.partnerURL = partnerURL;
		return this;
	}
	
	public TransactionEventRequest withSessionId(String sessionId) {
		this.sessionId = sessionId;
		return this;
	}
	
	public TransactionEventRequest withMongoDatabase(MongoDatabase mongoDatabase) {
		this.mongoDatabase = mongoDatabase;
		return this;
	}

	public TransactionEventRequest withUserId(ObjectId userId) {
		this.userId = userId;
		return this;
	}
	
	public TransactionEventRequest withOrganizationId(ObjectId organizationId) {
		this.organizationId = organizationId;
		return this;
	}
	
	public TransactionEventRequest withNotification(Notification notification) {
		this.notification = notification;
		return this;
	}

	public LambdaLogger getLogger() {
		return logger;
	}

	public void setLogger(LambdaLogger logger) {
		this.logger = logger;
	}

	public String getPartnerURL() {
		return partnerURL;
	}

	public String getSessionId() {
		return sessionId;
	}

	public MongoDatabase getMongoDatabase() {
		return mongoDatabase;
	}

	public ObjectId getUserId() {
		return userId;
	}

	public ObjectId getOrganizationId() {
		return organizationId;
	}

	public Notification getNotification() {
		return notification;
	}
}