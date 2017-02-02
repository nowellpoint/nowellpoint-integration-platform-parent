package com.nowellpoint.mongodb.document;

import java.io.Serializable;
import java.util.Date;

import org.bson.types.ObjectId;

import com.nowellpoint.mongodb.annotation.Id;
import com.nowellpoint.mongodb.annotation.MappedSuperclass;

@MappedSuperclass
public abstract class MongoDocument implements Serializable {
	
	private static final long serialVersionUID = 4165321769330346404L;
	
	@Id
	private ObjectId id;
	
	private Date createdOn;
	
	private Date lastUpdatedOn;
	
	public MongoDocument() {
		
	}
	
	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
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
}