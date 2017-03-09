package com.nowellpoint.api.rest.domain;

import java.time.Instant;
import java.util.Date;
import java.util.Set;

import com.nowellpoint.api.model.dynamodb.VaultEntry;
import com.nowellpoint.client.sforce.model.Identity;
import com.nowellpoint.client.sforce.model.Organization;
import com.nowellpoint.client.sforce.model.Theme;
import com.nowellpoint.client.sforce.model.sobject.Sobject;
import com.nowellpoint.mongodb.document.MongoDocument;

public class SalesforceConnector extends AbstractResource {
	
	private String name;
	
	private UserInfo createdBy;
	
	private UserInfo lastUpdatedBy;
	
	private UserInfo owner;

	private Identity identity;
	
	private Organization organization;
	
	private String connectionString;
	
	private Boolean isValid;
	
	private String serviceEndpoint;
	
	private String status;
	
	private String tag;
	
	private Set<Sobject> sobjects;
	
	private Theme theme;
	
	public SalesforceConnector() {
		
	}
	
	private SalesforceConnector(
			String id,
			String name, 
			UserInfo createdBy, 
			UserInfo lastUpdatedBy, 
			UserInfo owner, 
			Date createdOn, 
			Date lastUpdatedOn, 
			Identity identity, 
			Organization organization, 
			String connectionString, 
			Boolean isValid, 
			String serviceEndpoint,
			String tag) {
		
		this.id = id;
		this.name = name;
		this.createdBy = createdBy;
		this.lastUpdatedBy = lastUpdatedBy;
		this.owner = owner;
		this.createdOn = createdOn;
		this.lastUpdatedOn = lastUpdatedOn;
		this.identity = identity;
		this.organization = organization;
		this.connectionString = connectionString;
		this.isValid = isValid;
		this.serviceEndpoint = serviceEndpoint;
		this.tag = tag;
	}
	
	public static SalesforceConnector createSalesforceConnector(
			String createdById,
			String ownerId, 
			Identity identity, 
			Organization organization, 
			VaultEntry connectionEntry, 
			Boolean isValid, 
			String serviceEndpoint,
			String tag) {
		
		String name = organization.getName().concat(":").concat(organization.getId());
		Date now = Date.from(Instant.now());
		
		return new SalesforceConnector(
				null, 
				name, 
				new UserInfo(createdById), 
				new UserInfo(createdById), 
				new UserInfo(ownerId), 
				now, 
				now, 
				identity, 
				organization, 
				connectionEntry.getToken(), 
				isValid, 
				serviceEndpoint, 
				tag);
	}
	
	public static SalesforceConnector createSalesforceConnector(
			String name, 
			String createdById,
			String ownerId, 
			Identity identity, 
			Organization organization, 
			VaultEntry connectionEntry, 
			Boolean isValid, 
			String serviceEndpoint,
			String tag) {
		
		Date now = Date.from(Instant.now());

		return new SalesforceConnector(
				null, 
				name, 
				new UserInfo(createdById), 
				new UserInfo(createdById), 
				new UserInfo(ownerId), 
				now, 
				now, 
				identity, 
				organization, 
				connectionEntry.getToken(), 
				isValid, 
				serviceEndpoint, 
				tag);
	}
	
	private <T> SalesforceConnector(T document) {
		modelMapper.map(document, this);
	}
	
	public static SalesforceConnector of(MongoDocument document) {
		return new SalesforceConnector(document);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public UserInfo getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(UserInfo createdBy) {
		this.createdBy = createdBy;
	}

	public UserInfo getLastUpdatedBy() {
		return lastUpdatedBy;
	}

	public void setLastUpdatedBy(UserInfo lastUpdatedBy) {
		this.lastUpdatedBy = lastUpdatedBy;
	}

	public UserInfo getOwner() {
		return owner;
	}

	public void setOwner(UserInfo owner) {
		this.owner = owner;
	}

	public Identity getIdentity() {
		return identity;
	}

	public void setIdentity(Identity identity) {
		this.identity = identity;
	}

	public Organization getOrganization() {
		return organization;
	}

	public void setOrganization(Organization organization) {
		this.organization = organization;
	}	
	
	public String getConnectionString() {
		return connectionString;
	}

	public void setConnectionString(String connectionString) {
		this.connectionString = connectionString;
	}

	public Boolean getIsValid() {
		return isValid;
	}

	public void setIsValid(Boolean isValid) {
		this.isValid = isValid;
	}

	public String getServiceEndpoint() {
		return serviceEndpoint;
	}

	public void setServiceEndpoint(String serviceEndpoint) {
		this.serviceEndpoint = serviceEndpoint;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}
	
	public Set<Sobject> getSobjects() {
		return sobjects;
	}

	public void setSobjects(Set<Sobject> sobjects) {
		this.sobjects = sobjects;
	}

	public Theme getTheme() {
		return theme;
	}

	public void setTheme(Theme theme) {
		this.theme = theme;
	}

	@Override
	public MongoDocument toDocument() {
		return modelMapper.map(this, com.nowellpoint.api.model.document.SalesforceConnector.class);
	}
}