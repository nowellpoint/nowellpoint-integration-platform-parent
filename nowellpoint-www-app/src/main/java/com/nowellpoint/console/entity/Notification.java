package com.nowellpoint.console.entity;

import java.io.Serializable;
import java.util.Date;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Version;

@Entity(value = "notifications")
public class Notification implements Serializable {
	
	private static final long serialVersionUID = 9162304609981811453L;
	
	@Id
	private ObjectId id;
	
	@Version
	private Long version;
	
	private String who;
	
	private Date receivedOn;
	
	private String subject;
	
	private String message;
	
	private Boolean isRead;
	
	private Boolean isUrgent;
	
	private ObjectId organizationId;
	
	public Notification() {
		
	}

	public String getSubject() {
		return subject;
	}

	public String getMessage() {
		return message;
	}

	public Boolean getIsRead() {
		return isRead;
	}

	public Boolean getIsUrgent() {
		return isUrgent;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public void setIsRead(Boolean isRead) {
		this.isRead = isRead;
	}

	public void setIsUrgent(Boolean isUrgent) {
		this.isUrgent = isUrgent;
	}
	
	public String getWho() {
		return who;
	}

	public ObjectId getOrganizationId() {
		return organizationId;
	}

	public void setWho(String who) {
		this.who = who;
	}

	public ObjectId getId() {
		return id;
	}

	public Long getVersion() {
		return version;
	}

	public Date getReceivedOn() {
		return receivedOn;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public void setVersion(Long version) {
		this.version = version;
	}

	public void setReceivedOn(Date receivedOn) {
		this.receivedOn = receivedOn;
	}

	public void setOrganizationId(ObjectId organizationId) {
		this.organizationId = organizationId;
	}
}