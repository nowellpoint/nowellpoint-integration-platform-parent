package com.nowellpoint.client.model;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class AbstractResource implements Resource {

	private String id;
	
	private Date createdDate;
	
	private Date lastModifiedDate;
	
	private UserInfo createdBy;
	
	private UserInfo lastModifiedBy;
	
	private Meta meta;

	public AbstractResource() {
		
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
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
	
	public UserInfo getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(UserInfo createdBy) {
		this.createdBy = createdBy;
	}

	public UserInfo getLastModifiedBy() {
		return lastModifiedBy;
	}

	public void setLastModifiedBy(UserInfo lastModifiedBy) {
		this.lastModifiedBy = lastModifiedBy;
	}

	public Meta getMeta() {
		return meta;
	}

	public void setMeta(Meta meta) {
		this.meta = meta;
	}
}