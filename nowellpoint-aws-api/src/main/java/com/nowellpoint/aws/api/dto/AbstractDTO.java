package com.nowellpoint.aws.api.dto;

import java.util.Date;

import javax.validation.constraints.Null;

import java.io.Serializable;
import java.net.URI;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonIgnore;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_EMPTY)
public abstract class AbstractDTO implements Serializable {
	
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
	
	/**
	 * 
	 */
	
	private Date systemCreationDate;
	
	/**
	 * 
	 */

	private Date systemModifiedDate;
	
	/**
	 * 
	 */
	
	@JsonIgnore
	private URI eventSource;
	
	/**
	 * 
	 */
	
	@JsonIgnore
	private String subject;
	
	
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

	public URI getEventSource() {
		return eventSource;
	}

	public void setEventSource(URI eventSource) {
		this.eventSource = eventSource;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}
}