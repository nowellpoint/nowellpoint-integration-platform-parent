package com.nowellpoint.console.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

public class Dashboard implements Serializable {
	
	private static final long serialVersionUID = -6196431095732154837L;

	private Date lastRefreshedOn;
	
	private MetadataComponent customObject;
	
	private MetadataComponent apexClass;
	
	private MetadataComponent apexTrigger;
	
	private MetadataComponent recordType;
	
	private MetadataComponent userRole;
	
	private MetadataComponent profile;
	
	private Set<UserLicense> userLicenses;
	
	private Limits limits;
	
	public Dashboard() {
		
	}

	public Date getLastRefreshedOn() {
		return lastRefreshedOn;
	}

	public void setLastRefreshedOn(Date lastRefreshedOn) {
		this.lastRefreshedOn = lastRefreshedOn;
	}

	public MetadataComponent getCustomObject() {
		return customObject;
	}

	public void setCustomObject(MetadataComponent customObject) {
		this.customObject = customObject;
	}

	public MetadataComponent getApexClass() {
		return apexClass;
	}

	public MetadataComponent getApexTrigger() {
		return apexTrigger;
	}

	public MetadataComponent getRecordType() {
		return recordType;
	}

	public MetadataComponent getUserRole() {
		return userRole;
	}

	public MetadataComponent getProfile() {
		return profile;
	}

	public void setApexClass(MetadataComponent apexClass) {
		this.apexClass = apexClass;
	}

	public void setApexTrigger(MetadataComponent apexTrigger) {
		this.apexTrigger = apexTrigger;
	}

	public void setRecordType(MetadataComponent recordType) {
		this.recordType = recordType;
	}

	public void setUserRole(MetadataComponent userRole) {
		this.userRole = userRole;
	}

	public void setProfile(MetadataComponent profile) {
		this.profile = profile;
	}

	public Set<UserLicense> getUserLicenses() {
		return userLicenses;
	}

	public void setUserLicenses(Set<UserLicense> userLicenses) {
		this.userLicenses = userLicenses;
	}
	
	public Limits getLimits() {
		return limits;
	}

	public void setLimits(Limits limits) {
		this.limits = limits;
	}
}