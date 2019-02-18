package com.nowellpoint.console.entity;

import java.io.Serializable;
import java.util.Date;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Reference;
import org.mongodb.morphia.annotations.Version;

abstract class BaseEntity implements Serializable {

	private static final long serialVersionUID = -5484847535380950162L;

	@Id
	private ObjectId id;
	
	@Version
	private Long version;
	
	private Date createdOn;
	
	@Reference
	private Identity createdBy;
	
	private Date lastUpdatedOn;
	
	@Reference
	private Identity lastUpdatedBy;
	
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

	public Identity getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(Identity createdBy) {
		this.createdBy = createdBy;
	}

	public Identity getLastUpdatedBy() {
		return lastUpdatedBy;
	}

	public void setLastUpdatedBy(Identity lastUpdatedBy) {
		this.lastUpdatedBy = lastUpdatedBy;
	}
}