package com.nowellpoint.aws.api.dto;

import com.nowellpoint.client.sforce.model.Identity;
import com.nowellpoint.client.sforce.model.Organization;

public class SalesforceInstanceDTO extends AbstractDTO {

	private static final long serialVersionUID = -6847034908687287362L;

	private Identity identity;
	
	private Organization organization;
	
	public SalesforceInstanceDTO() {
		
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