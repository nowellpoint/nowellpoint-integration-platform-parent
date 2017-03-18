package com.nowellpoint.api.rest.domain;

import java.time.Instant;
import java.util.Date;
import java.util.HashSet;
import java.util.Optional;
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
	
	private Date lastTestedOn;
	
	private Boolean isValid;
	
	private String serviceEndpoint;
	
	private String status;
	
	private String tag;
	
	private Theme theme;
	
	private Set<Service> services = new HashSet<>();
	
	private Set<Sobject> sobjects = new HashSet<>();
	
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
			String status,
			Date lastTestedOn) {
		
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
		this.status = status;
		this.lastTestedOn = lastTestedOn;
	}
	
	public static SalesforceConnector createSalesforceConnector(
			String createdById,
			String ownerId, 
			Identity identity, 
			Organization organization, 
			VaultEntry connectionEntry, 
			Boolean isValid, 
			String serviceEndpoint,
			String status,
			Date lastTestedOn) {
		
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
				status,
				lastTestedOn);
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
			String status,
			Date lastTestedOn) {
		
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
				status,
				lastTestedOn);
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

	public Date getLastTestedOn() {
		return lastTestedOn;
	}

	public void setLastTestedOn(Date lastTestedOn) {
		this.lastTestedOn = lastTestedOn;
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
	
	public void addService(Service service) {
		if (services == null) {
			services = new HashSet<>();
		} else {
			if (services.contains(service)) {
				throw new IllegalArgumentException(String.format("Unable to add %s since it has already been added to Salesforce Connector", service.getName()));
			}
		}
		
		services.add(service);
	}
	
	public Service getService(String serviceId) {
		
		if (services == null) {
			services = new HashSet<>();
		}
		
		Optional<Service> optional = services.stream()
				.filter(s -> serviceId.equals(s.getServiceId()))
				.findFirst();
		
		if (! optional.isPresent()) {
			throw new IllegalArgumentException(String.format("Service Id: %s does not exist", serviceId));
		}
		
		return optional.get();
	}
	
	public Set<Service> getServices() {
		return services;
	}

	public void setServices(Set<Service> services) {
		this.services = services;
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