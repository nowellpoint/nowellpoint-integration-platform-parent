package com.nowellpoint.console.entity;

import java.util.Date;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Reference;
import org.mongodb.morphia.annotations.Version;

abstract class BaseEntity {

	@Id
	private ObjectId id;
	
	@Version
	private Long version;
	
	private Date createdOn;
	
	@Reference
	private UserProfile createdBy;
	
	private Date lastUpdatedOn;
	
	@Reference
	private UserProfile lastUpdatedBy;
	
	public BaseEntity() {
		
	}
	
	public BaseEntity(String id) {
		setId(new ObjectId(id));
	}
	
	public BaseEntity(ObjectId id) {
		setId(id);
	}

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public Long getVersion() {
		return version;
	}

	public void setVersion(Long version) {
		this.version = version;
	}

	public Date getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}

	public Date getLastUpdatedOn() {
		return lastUpdatedOn;
	}

	public void setLastUpdatedOn(Date lastUpdatedOn) {
		this.lastUpdatedOn = lastUpdatedOn;
	}

	public UserProfile getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(UserProfile createdBy) {
		this.createdBy = createdBy;
	}

	public UserProfile getLastUpdatedBy() {
		return lastUpdatedBy;
	}

	public void setLastUpdatedBy(UserProfile lastUpdatedBy) {
		this.lastUpdatedBy = lastUpdatedBy;
	}
}