package com.nowellpoint.www.app.model;

import java.util.ArrayList;
import java.util.List;

import com.nowellpoint.www.app.model.sforce.Identity;
import com.nowellpoint.www.app.model.sforce.Organization;

public class SalesforceConnector extends Resource {

	private Identity identity;
	
	private Organization organization;
	
	private AccountProfile owner;
	
	private String tag;
	
	private List<ServiceInstance> serviceInstances;
	
	public SalesforceConnector() {
		serviceInstances = new ArrayList<ServiceInstance>();
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

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public List<ServiceInstance> getServiceInstances() {
		return serviceInstances;
	}

	public void setServiceInstances(List<ServiceInstance> serviceInstances) {
		this.serviceInstances = serviceInstances;
	}
}