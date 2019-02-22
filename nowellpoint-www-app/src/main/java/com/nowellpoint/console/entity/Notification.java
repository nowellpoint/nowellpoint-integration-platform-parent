package com.nowellpoint.console.entity;

import java.io.Serializable;
import java.util.Date;

public class Notification implements Serializable {
	private static final long serialVersionUID = 9162304609981811453L;
	private String subject;
	private String body;
	private Date receivedOn;
	private Boolean isRead;
	private Boolean isUrgent;
	
	public Notification() {
		
	}

	public String getSubject() {
		return subject;
	}

	public String getBody() {
		return body;
	}

	public Date getReceivedOn() {
		return receivedOn;
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

	public void setBody(String body) {
		this.body = body;
	}

	public void setReceivedOn(Date receivedOn) {
		this.receivedOn = receivedOn;
	}

	public void setIsRead(Boolean isRead) {
		this.isRead = isRead;
	}

	public void setIsUrgent(Boolean isUrgent) {
		this.isUrgent = isUrgent;
	}
}