package com.nowellpoint.www.app.model.sforce;

import com.nowellpoint.www.app.model.Resource;

public class SalesforceConnector extends Resource {

	private Identity identity;
	
	private Organization organization;
	
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
}