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
	
	private String sobject;
	
	private Boolean enabled;
	
	private String description;
	
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

	public String getSObject() {
		return sobject;
	}

	public void setSObject(String sobject) {
		this.sobject = sobject;
	}

	public Boolean getEnabled() {
		return enabled;
	}

	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
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