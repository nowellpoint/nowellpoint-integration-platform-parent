package com.nowellpoint.www.app.model.sforce;

import com.nowellpoint.www.app.model.Resource;

public class SalesforceInstance extends Resource {

	private Identity identity;
	
	private Organization organization;
	
	public SalesforceInstance() {
		
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
}