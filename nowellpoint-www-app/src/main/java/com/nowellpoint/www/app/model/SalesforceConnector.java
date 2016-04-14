package com.nowellpoint.www.app.model;

import java.util.List;

import com.nowellpoint.www.app.model.sforce.Identity;
import com.nowellpoint.www.app.model.sforce.Organization;

public class SalesforceConnector extends Resource {

	private Identity identity;
	
	private Organization organization;
	
	private AccountProfile owner;
	
	private List<ServiceInstance> serviceInstances;
	
	public SalesforceConnector() {
		
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

	public AccountProfile getOwner() {
		return owner;
	}

	public void setOwner(AccountProfile owner) {
		this.owner = owner;
	}

	public List<ServiceInstance> getServiceInstances() {
		return serviceInstances;
	}

	public void setServiceInstances(List<ServiceInstance> serviceInstances) {
		this.serviceInstances = serviceInstances;
	}
}