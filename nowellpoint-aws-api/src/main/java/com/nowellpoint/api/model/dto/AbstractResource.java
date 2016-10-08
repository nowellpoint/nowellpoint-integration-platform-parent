package com.nowellpoint.api.model.dto;

import java.util.Date;

import javax.validation.constraints.Null;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_EMPTY)
public abstract class AbstractResource implements Serializable {
	
	/**
	 * 
	 */
	
	private static final long serialVersionUID = 3468369885294317641L;

	/**
	 * 
	 */
	
	@Null
	private String id;
	
	/**
	 * 
	 */
	
	private Date createdDate;
	
	/**
	 * 
	 */
	
	private Date lastModifiedDate;
	
	/**
	 * 
	 */
	
	private Date systemCreationDate;
	
	/**
	 * 
	 */

	private Date systemModifiedDate;
	
	
	public AbstractResource() {
		
	}
	
	public AbstractResource(String id) {
		setId(id);
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
	
	public Date getSystemCreationDate() {
		return systemCreationDate;
	}

	public void setSystemCreationDate(Date systemCreationDate) {
		this.systemCreationDate = systemCreationDate;
	}

	public Date getSystemModifiedDate() {
		return systemModifiedDate;
	}

	public void setSystemModifiedDate(Date systemModifiedDate) {
		this.systemModifiedDate = systemModifiedDate;
	}
}