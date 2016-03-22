package com.nowellpoint.aws.data.mongodb;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.nowellpoint.aws.data.ServiceInstanceCodec;
import com.nowellpoint.aws.data.annotation.Document;
import com.nowellpoint.aws.data.mongodb.sforce.Contact;

@Document(collectionName="service.instances", codec=ServiceInstanceCodec.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class SalesforceInstance extends AbstractDocument {

	/**
	 * 
	 */
	
	private static final long serialVersionUID = -3438714915624952119L;
	
	private String division;
	
	private String fax;
	
	private String defaultLocaleSidKey;
	
	private String fiscalYearStartMonth;
	
	private String instanceName;
	
	private String languageLocaleKey;
	
	private String name;
	
	private String organizationType;
	
	private String phone;
	
	private String primaryContact;
	
	private List<Contact> contacts;
	
	public SalesforceInstance() {
		
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

	public List<Contact> getContacts() {
		return contacts;
	}
	
	public void setContacts(List<Contact> contacts) {
		this.contacts = contacts;
	}
	
	public void addContact(Contact contact) {
		if (contacts == null) {
			contacts = new ArrayList<Contact>();
		}
		contacts.add(contact);
	}
}