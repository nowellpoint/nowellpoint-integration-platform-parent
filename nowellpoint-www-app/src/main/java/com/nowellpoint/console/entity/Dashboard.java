package com.nowellpoint.console.entity;

import java.io.Serializable;
import java.util.Date;

public class Dashboard implements Serializable {
	
	private static final long serialVersionUID = -6196431095732154837L;

	private Date lastRefreshedOn;
	
	private Integer customObjectCount;
	
	public Dashboard() {
		
	}

	public Date getLastRefreshedOn() {
		return lastRefreshedOn;
	}

	public void setLastRefreshedOn(Date lastRefreshedOn) {
		this.lastRefreshedOn = lastRefreshedOn;
	}

	public Integer getCustomObjectCount() {
		return customObjectCount;
	}

	public void setCustomObjectCount(Integer customObjectCount) {
		this.customObjectCount = customObjectCount;
	}
}