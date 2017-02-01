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
	
	private Date createdDate;
	
	private Date lastModifiedDate;
	
	private Date systemCreatedDate;
	
	private Date systemModifiedDate;
	
	public MongoDocument() {
		
	}
	
	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public Date getLastModifiedDate() {
		return lastModifiedDate;
	}

	public void setLastModifiedDate(Date lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}

	public Date getSystemCreatedDate() {
		return systemCreatedDate;
	}

	public void setSystemCreatedDate(Date systemCreatedDate) {
		this.systemCreatedDate = systemCreatedDate;
	}

	public Date getSystemModifiedDate() {
		return systemModifiedDate;
	}

	public void setSystemModifiedDate(Date systemModifiedDate) {
		this.systemModifiedDate = systemModifiedDate;
	}
}