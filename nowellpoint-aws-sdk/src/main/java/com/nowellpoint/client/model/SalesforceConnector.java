package com.nowellpoint.client.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.nowellpoint.client.model.sforce.Identity;
import com.nowellpoint.client.model.sforce.Organization;
import com.nowellpoint.client.model.sforce.Sobject;
import com.nowellpoint.client.model.sforce.Theme;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SalesforceConnector extends AbstractResource {
	
	private String name;

	private Identity identity;
	
	private Organization organization;
	
	private String connectionString;
	
	private Date lastTestedOn;
	
	private Boolean isValid;
	
	private String serviceEndpoint;
	
	private String status;
	
	private UserInfo owner;
	
	private String tag;
	
	private List<Service> services;
	
	private List<Sobject> sobjects;
	
	private Theme theme;
	
	public SalesforceConnector() {
		sobjects = new ArrayList<>();
	}
	
	public SalesforceConnector(String id) {
		setId(id);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getServiceEndpoint() {
		return serviceEndpoint;
	}

	public void setServiceEndpoint(String serviceEndpoint) {
		this.serviceEndpoint = serviceEndpoint;
	}

	public UserInfo getOwner() {
		return owner;
	}

	public void setOwner(UserInfo owner) {
		this.owner = owner;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public List<Service> getServices() {
		return services;
	}

	public void setServices(List<Service> services) {
		this.services = services;
	}

	public List<Sobject> getSobjects() {
		return sobjects;
	}

	public void setSobjects(List<Sobject> sobjects) {
		this.sobjects = sobjects;
	}

	public Theme getTheme() {
		return theme;
	}

	public void setTheme(Theme theme) {
		this.theme = theme;
	}
}