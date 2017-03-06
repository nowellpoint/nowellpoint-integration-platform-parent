package com.nowellpoint.client.model;

import java.util.Set;

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
	
	private ConnectString connectString;
	
	private Boolean isValid;
	
	private String serviceEndpoint;
	
	private String connectStatus;
	
	private UserInfo owner;
	
	private String tag;
	
	private Set<Sobject> sobjects;
	
	private Theme theme;
	
	public SalesforceConnector() {
		
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

	public ConnectString getConnectString() {
		return connectString;
	}

	public void setConnectString(ConnectString connectString) {
		this.connectString = connectString;
	}

	public Boolean getIsValid() {
		return isValid;
	}

	public void setIsValid(Boolean isValid) {
		this.isValid = isValid;
	}

	public String getConnectStatus() {
		return connectStatus;
	}

	public void setConnectStatus(String connectStatus) {
		this.connectStatus = connectStatus;
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
}