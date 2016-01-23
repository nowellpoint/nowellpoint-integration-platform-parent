package com.nowellpoint.aws.data;

import java.util.Date;

public class AbstractDTO {
	
	private String id;

	/**
	 * 
	 */
	
	private String createdById;
	
	/**
	 * 
	 */
	
	private String lastModifiedById;
	
	/**
	 * 
	 */
	
	private Date createdDate;
	
	/**
	 * 
	 */
	
	private Date lastModifiedDate;
	
	public AbstractDTO() {
		
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCreatedById() {
		return createdById;
	}

	public void setCreatedById(String createdById) {
		this.createdById = createdById;
	}

	public String getLastModifiedById() {
		return lastModifiedById;
	}

	public void setLastModifiedById(String lastModifiedById) {
		this.lastModifiedById = lastModifiedById;
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
}
