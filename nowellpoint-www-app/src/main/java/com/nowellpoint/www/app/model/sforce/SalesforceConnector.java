package com.nowellpoint.www.app.model.sforce;

import com.nowellpoint.www.app.model.AccountProfile;
import com.nowellpoint.www.app.model.Resource;

public class SalesforceConnector extends Resource {

	private Identity identity;
	
	private Organization organization;
	
	private AccountProfile owner;
	
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
}