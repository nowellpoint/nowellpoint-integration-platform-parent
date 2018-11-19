package com.nowellpoint.client.sforce.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UserLicense implements Serializable {

	private static final long serialVersionUID = 7727327086199417077L;
	
	@JsonProperty("attributes")
	private Attributes attributes;

	@JsonProperty("Id")
	private String id;
	
	@JsonProperty("LicenseDefinitionKey")
	private String licenseDefinitionKey;
	
	@JsonProperty("MasterLabel")
	private String masterLabel;
	
	@JsonProperty("Name")
	private String name;
	
	@JsonProperty("Status")
	private String status;
	
	@JsonProperty("TotalLicenses")
	private Integer totalLicenses;
	
	@JsonProperty("UsedLicenses")
	private Integer usedLicenses;
	
	public UserLicense() {
		
	}

	public Attributes getAttributes() {
		return attributes;
	}

	public void setAttributes(Attributes attributes) {
		this.attributes = attributes;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getLicenseDefinitionKey() {
		return licenseDefinitionKey;
	}

	public void setLicenseDefinitionKey(String licenseDefinitionKey) {
		this.licenseDefinitionKey = licenseDefinitionKey;
	}

	public String getMasterLabel() {
		return masterLabel;
	}

	public void setMasterLabel(String masterLabel) {
		this.masterLabel = masterLabel;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Integer getTotalLicenses() {
		return totalLicenses;
	}

	public void setTotalLicenses(Integer totalLicenses) {
		this.totalLicenses = totalLicenses;
	}

	public Integer getUsedLicenses() {
		return usedLicenses;
	}

	public void setUsedLicenses(Integer usedLicenses) {
		this.usedLicenses = usedLicenses;
	}
}