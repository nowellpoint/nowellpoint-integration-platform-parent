package com.nowellpoint.aws.data.mongodb;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.nowellpoint.aws.data.SalesforceInstanceCodec;
import com.nowellpoint.aws.data.annotation.Document;
import com.nowellpoint.aws.data.mongodb.sforce.Identity;
import com.nowellpoint.aws.data.mongodb.sforce.Organization;

@Document(collectionName="salesforce.connectors", codec=SalesforceInstanceCodec.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class SalesforceConnector extends AbstractDocument {

	/**
	 * 
	 */
	
	private static final long serialVersionUID = -3438714915624952119L;
	
	private User owner;
	
	private Identity identity;
	
	private Organization organization;
	
	public SalesforceConnector() {
		
	}

	public User getOwner() {
		return owner;
	}

	public void setOwner(User owner) {
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
}