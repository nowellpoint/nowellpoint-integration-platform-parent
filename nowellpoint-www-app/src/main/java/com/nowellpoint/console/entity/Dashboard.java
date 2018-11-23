package com.nowellpoint.console.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

public class Dashboard implements Serializable {
	
	private static final long serialVersionUID = -6196431095732154837L;

	private Date lastRefreshedOn;
	
	private Integer customObjectCount;
	
	private Integer apexClassCount;
	
	private Integer apexTriggerCount;
	
	private Integer recordTypeCount;
	
	private Set<UserLicense> userLicenses;
	
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

	public Integer getApexClassCount() {
		return apexClassCount;
	}

	public void setApexClassCount(Integer apexClassCount) {
		this.apexClassCount = apexClassCount;
	}

	public Integer getApexTriggerCount() {
		return apexTriggerCount;
	}

	public void setApexTriggerCount(Integer apexTriggerCount) {
		this.apexTriggerCount = apexTriggerCount;
	}

	public Integer getRecordTypeCount() {
		return recordTypeCount;
	}

	public void setRecordTypeCount(Integer recordTypeCount) {
		this.recordTypeCount = recordTypeCount;
	}

	public Set<UserLicense> getUserLicenses() {
		return userLicenses;
	}

	public void setUserLicenses(Set<UserLicense> userLicenses) {
		this.userLicenses = userLicenses;
	}
}