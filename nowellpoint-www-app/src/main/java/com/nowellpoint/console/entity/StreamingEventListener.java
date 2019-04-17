package com.nowellpoint.console.entity;

import java.io.Serializable;
import java.util.Date;

import org.mongodb.morphia.annotations.Reference;

public class StreamingEventListener implements Serializable {
	
	private static final long serialVersionUID = 1631326328342999076L;

	private String topicId;
	
	private String prefix;
	
	private String name;
	
	private String source;
	
	private String description;
	
	private Boolean notifyForOperationCreate;
	
	private Boolean notifyForOperationUpdate;
	
	private Boolean notifyForOperationUndelete;
	
	private Boolean notifyForOperationDelete;
	
	private Boolean active;
	
	private Boolean custom;
	
	private Date createdOn;
	
	@Reference
	private Identity createdBy;
	
	private Date lastUpdatedOn;
	
	@Reference
	private Identity lastUpdatedBy;
	
	private Date startedOn;
	
	private Date stoppedOn;
	
	public StreamingEventListener() {
		
	}

	public String getTopicId() {
		return topicId;
	}

	public void setTopicId(String topicId) {
		this.topicId = topicId;
	}

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Boolean getNotifyForOperationCreate() {
		return notifyForOperationCreate;
	}

	public void setNotifyForOperationCreate(Boolean notifyForOperationCreate) {
		this.notifyForOperationCreate = notifyForOperationCreate;
	}

	public Boolean getNotifyForOperationUpdate() {
		return notifyForOperationUpdate;
	}

	public void setNotifyForOperationUpdate(Boolean notifyForOperationUpdate) {
		this.notifyForOperationUpdate = notifyForOperationUpdate;
	}

	public Boolean getNotifyForOperationUndelete() {
		return notifyForOperationUndelete;
	}

	public void setNotifyForOperationUndelete(Boolean notifyForOperationUndelete) {
		this.notifyForOperationUndelete = notifyForOperationUndelete;
	}

	public Boolean getNotifyForOperationDelete() {
		return notifyForOperationDelete;
	}

	public void setNotifyForOperationDelete(Boolean notifyForOperationDelete) {
		this.notifyForOperationDelete = notifyForOperationDelete;
	}

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	public Boolean getCustom() {
		return custom;
	}

	public void setCustom(Boolean custom) {
		this.custom = custom;
	}

	public Date getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}

	public Identity getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(Identity createdBy) {
		this.createdBy = createdBy;
	}

	public Date getLastUpdatedOn() {
		return lastUpdatedOn;
	}

	public void setLastUpdatedOn(Date lastUpdatedOn) {
		this.lastUpdatedOn = lastUpdatedOn;
	}

	public Identity getLastUpdatedBy() {
		return lastUpdatedBy;
	}

	public void setLastUpdatedBy(Identity lastUpdatedBy) {
		this.lastUpdatedBy = lastUpdatedBy;
	}

	public Date getStartedOn() {
		return startedOn;
	}

	public void setStartedOn(Date startedOn) {
		this.startedOn = startedOn;
	}

	public Date getStoppedOn() {
		return stoppedOn;
	}

	public void setStoppedOn(Date stoppedOn) {
		this.stoppedOn = stoppedOn;
	}
}