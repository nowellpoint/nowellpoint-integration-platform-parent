package com.nowellpoint.console.entity;

import java.io.Serializable;
import java.util.Date;

public class StreamingEventListener implements Serializable {

	/**
	 * 
	 */
	
	private static final long serialVersionUID = 1631326328342999076L;

	private String id;
	
	private String prefix;
	
	private String name;
	
	private String source;
	
	private String description;
	
	private Boolean notifyForOperationCreate;
	
	private Boolean notifyForOperationUpdate;
	
	private Boolean notifyForOperationUndelete;
	
	private Boolean notifyForOperationDelete;
	
	private Boolean active;
	
	private Date lastEventReceivedOn;
	
	private Long replayId;
	
	public StreamingEventListener() {
		
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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

	public Boolean isActive() {
		return active;
	}

	public void isActive(Boolean active) {
		this.active = active;
	}

	public Date getLastEventReceivedOn() {
		return lastEventReceivedOn;
	}

	public void setLastEventReceivedOn(Date lastEventReceivedOn) {
		this.lastEventReceivedOn = lastEventReceivedOn;
	}

	public Long getReplayId() {
		return replayId;
	}

	public void setReplayId(Long replayId) {
		this.replayId = replayId;
	}
}