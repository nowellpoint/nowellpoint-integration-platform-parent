package com.nowellpoint.aws.lambda.s3;

import org.bson.types.ObjectId;

import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.mongodb.client.MongoDatabase;
import com.nowellpoint.aws.lambda.sforce.model.Notification;

public class OutboundMessageEventRequest {
	
	private LambdaLogger logger;
	
	private String partnerURL;
	
	private String sessionId;
	
	private MongoDatabase mongoDatabase;
	
	private ObjectId userId; 
	
	private ObjectId organizationId;
	
	private Notification notification;
	
	public OutboundMessageEventRequest() {
		
	}
	
	public OutboundMessageEventRequest withLogger(LambdaLogger logger) {
		this.logger = logger;
		return this;
	}
	
	public OutboundMessageEventRequest withPartnerURL(String partnerURL) {
		this.partnerURL = partnerURL;
		return this;
	}
	
	public OutboundMessageEventRequest withSessionId(String sessionId) {
		this.sessionId = sessionId;
		return this;
	}
	
	public OutboundMessageEventRequest withMongoDatabase(MongoDatabase mongoDatabase) {
		this.mongoDatabase = mongoDatabase;
		return this;
	}

	public OutboundMessageEventRequest withUserId(ObjectId userId) {
		this.userId = userId;
		return this;
	}
	
	public OutboundMessageEventRequest withOrganizationId(ObjectId organizationId) {
		this.organizationId = organizationId;
		return this;
	}
	
	public OutboundMessageEventRequest withNotification(Notification notification) {
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