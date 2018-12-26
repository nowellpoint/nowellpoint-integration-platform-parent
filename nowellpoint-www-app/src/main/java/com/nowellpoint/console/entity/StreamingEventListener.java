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