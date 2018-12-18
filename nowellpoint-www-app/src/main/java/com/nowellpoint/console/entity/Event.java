package com.nowellpoint.console.entity;

import java.io.Serializable;
import java.util.Date;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

@Entity(value = "events")
public class Event implements Serializable {

	private static final long serialVersionUID = -226649098013040674L;
	
	@Id
	private ObjectId id;
	
	private ObjectId organizationId;
	
	private String salesforceId;
	
	private Date createdDate;
	
	private String createdById;
	
	private Date lastModifiedDate;
	
	private String lastModifiedById;
	
	private Date eventDate;
	
	private Long replayId;
	
	private String type;
	
	public Event() {
		
	}

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public ObjectId getOrganizationId() {
		return organizationId;
	}

	public void setOrganizationId(ObjectId organizationId) {
		this.organizationId = organizationId;
	}

	public String getSalesforceId() {
		return salesforceId;
	}

	public void setSalesforceId(String salesforceId) {
		this.salesforceId = salesforceId;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public String getCreatedById() {
		return createdById;
	}

	public void setCreatedById(String createdById) {
		this.createdById = createdById;
	}

	public Date getLastModifiedDate() {
		return lastModifiedDate;
	}

	public void setLastModifiedDate(Date lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}

	public String getLastModifiedById() {
		return lastModifiedById;
	}

	public void setLastModifiedById(String lastModifiedById) {
		this.lastModifiedById = lastModifiedById;
	}

	public Date getEventDate() {
		return eventDate;
	}

	public void setEventDate(Date eventDate) {
		this.eventDate = eventDate;
	}

	public Long getReplayId() {
		return replayId;
	}

	public void setReplayId(Long replayId) {
		this.replayId = replayId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
}