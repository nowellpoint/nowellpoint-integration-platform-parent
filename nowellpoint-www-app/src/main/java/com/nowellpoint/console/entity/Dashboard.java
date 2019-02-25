package com.nowellpoint.console.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

public class Dashboard implements Serializable {
	
	private static final long serialVersionUID = -6196431095732154837L;

	private Date lastRefreshedOn;
	
	private DashboardComponent customObject;
	
	private DashboardComponent apexClass;
	
	private DashboardComponent apexTrigger;
	
	private DashboardComponent recordType;
	
	private DashboardComponent userRole;
	
	private DashboardComponent profile;
	
	private Set<UserLicense> userLicenses;
	
	public Dashboard() {
		
	}

	public Date getLastRefreshedOn() {
		return lastRefreshedOn;
	}

	public void setLastRefreshedOn(Date lastRefreshedOn) {
		this.lastRefreshedOn = lastRefreshedOn;
	}

	public DashboardComponent getCustomObject() {
		return customObject;
	}

	public void setCustomObject(DashboardComponent customObject) {
		this.customObject = customObject;
	}

	public DashboardComponent getApexClass() {
		return apexClass;
	}

	public DashboardComponent getApexTrigger() {
		return apexTrigger;
	}

	public DashboardComponent getRecordType() {
		return recordType;
	}

	public DashboardComponent getUserRole() {
		return userRole;
	}

	public DashboardComponent getProfile() {
		return profile;
	}

	public void setApexClass(DashboardComponent apexClass) {
		this.apexClass = apexClass;
	}

	public void setApexTrigger(DashboardComponent apexTrigger) {
		this.apexTrigger = apexTrigger;
	}

	public void setRecordType(DashboardComponent recordType) {
		this.recordType = recordType;
	}

	public void setUserRole(DashboardComponent userRole) {
		this.userRole = userRole;
	}

	public void setProfile(DashboardComponent profile) {
		this.profile = profile;
	}

	public Set<UserLicense> getUserLicenses() {
		return userLicenses;
	}

	public void setUserLicenses(Set<UserLicense> userLicenses) {
		this.userLicenses = userLicenses;
	}
}