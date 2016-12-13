package com.nowellpoint.client.model.sforce;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Organization {
	
	@JsonProperty(value="Id")
	private String id;
	
	@JsonProperty(value="Division")
	private String division;
	
	@JsonProperty(value="DefaultLocaleSidKey")
	private String defaultLocaleSidKey;
	
	@JsonProperty(value="FiscalYearStartMonth")
	private String fiscalYearStartMonth;
	
	@JsonProperty(value="InstanceName")
	private String instanceName;
	
	@JsonProperty(value="LanguageLocaleKey")
	private String languageLocaleKey;
	
	@JsonProperty(value="Name")
	private String name;
	
	@JsonProperty(value="OrganizationType")
	private String organizationType;
	
	@JsonProperty(value="Phone")
	private String phone;
	
	@JsonProperty(value="PrimaryContact")
	private String primaryContact;
	
	@JsonProperty(value="UsesStartDateAsFiscalYearName")
	private Boolean usesStartDateAsFiscalYearName;
	
	public Organization() {

	}
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}

	public String getDivision() {
		return division;
	}

	public void setDivision(String division) {
		this.division = division;
	}

	public String getDefaultLocaleSidKey() {
		return defaultLocaleSidKey;
	}

	public void setDefaultLocaleSidKey(String defaultLocaleSidKey) {
		this.defaultLocaleSidKey = defaultLocaleSidKey;
	}

	public String getFiscalYearStartMonth() {
		return fiscalYearStartMonth;
	}

	public void setFiscalYearStartMonth(String fiscalYearStartMonth) {
		this.fiscalYearStartMonth = fiscalYearStartMonth;
	}

	public String getInstanceName() {
		return instanceName;
	}

	public void setInstanceName(String instanceName) {
		this.instanceName = instanceName;
	}

	public String getLanguageLocaleKey() {
		return languageLocaleKey;
	}

	public void setLanguageLocaleKey(String languageLocaleKey) {
		this.languageLocaleKey = languageLocaleKey;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getOrganizationType() {
		return organizationType;
	}

	public void setOrganizationType(String organizationType) {
		this.organizationType = organizationType;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getPrimaryContact() {
		return primaryContact;
	}

	public void setPrimaryContact(String primaryContact) {
		this.primaryContact = primaryContact;
	}

	public Boolean getUsesStartDateAsFiscalYearName() {
		return usesStartDateAsFiscalYearName;
	}

	public void setUsesStartDateAsFiscalYearName(Boolean usesStartDateAsFiscalYearName) {
		this.usesStartDateAsFiscalYearName = usesStartDateAsFiscalYearName;
	}
}