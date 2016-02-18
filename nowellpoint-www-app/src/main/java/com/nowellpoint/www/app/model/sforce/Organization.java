package com.nowellpoint.www.app.model.sforce;

import java.util.List;

import com.nowellpoint.www.app.model.BaseEntity;

public class Organization extends BaseEntity {

	/**
	 * 
	 */
	
	private static final long serialVersionUID = 6158058332734118874L;
	
	private Attributes attributes;
	
	private String division;
	
	private String fax;
	
	private String defaultLocaleSidKey;
	
	private String fiscalYearStartMonth;
	
	private String instanceName;
	
	private Boolean isSandbox;
	
	private String languageLocaleKey;
	
	private String name;
	
	private String organizationType;
	
	private String phone;
	
	private String primaryContact;
	
	private Boolean usesStartDateAsFiscalYearName;
	
	private List<OrganizationContact> organizationContacts;
	
	public Organization() {
		
	}

	public Attributes getAttributes() {
		return attributes;
	}

	public void setAttributes(Attributes attributes) {
		this.attributes = attributes;
	}

	public String getDivision() {
		return division;
	}

	public void setDivision(String division) {
		this.division = division;
	}

	public String getFax() {
		return fax;
	}

	public void setFax(String fax) {
		this.fax = fax;
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

	public Boolean getIsSandbox() {
		return isSandbox;
	}

	public void setIsSandbox(Boolean isSandbox) {
		this.isSandbox = isSandbox;
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
	
	public List<OrganizationContact> getOrganizationContacts() {
		return organizationContacts;
	}
	
	public void setOrganizationContacts(List<OrganizationContact> organizationContacts) {
		this.organizationContacts = organizationContacts;
	}
}