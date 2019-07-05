package com.nowellpoint.listener.model;

import java.util.Date;

import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.bson.types.ObjectId;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
public class Notification {
	private Boolean isRead;
	private Boolean isUrgent;
	private String message;
	private ObjectId organizationId;
	private Date receivedOn;
	private String subject;
	private String receivedFrom;
	
	@BsonCreator
	public Notification(@BsonProperty("isRead") Boolean isRead,
			@BsonProperty("isUrgent") Boolean isUrgent,
			@BsonProperty("message") String message,
			@BsonProperty("organizationId") ObjectId organizationId,
			@BsonProperty("receivedOn") Date receivedOn,
			@BsonProperty("subject") String subject,
			@BsonProperty("receivedFrom") String receivedFrom) {
		
		this.isRead = isRead;
		this.isUrgent = isUrgent;
		this.message = message;
		this.organizationId = organizationId;
		this.receivedOn = receivedOn;
		this.subject = subject;
		this.receivedFrom = receivedFrom;
	}
}