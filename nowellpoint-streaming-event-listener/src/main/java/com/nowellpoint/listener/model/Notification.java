package com.nowellpoint.listener.model;

import java.io.Serializable;
import java.util.Date;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Entity(value = "notifications", noClassnameStored = true)
public class Notification implements Serializable {
	
	private static final long serialVersionUID = 2829335541506907521L;

	@Getter @Setter @Id private ObjectId id;
	@Getter @Setter private String receivedFrom;
	@Getter @Setter private Date receivedOn;
	@Getter @Setter private String subject;
	@Getter @Setter private String message;
	@Getter @Setter private Boolean isRead;
	@Getter @Setter private Boolean isUrgent;
	@Getter @Setter private ObjectId organizationId;
}