package com.nowellpoint.aws.api.model;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.nowellpoint.aws.api.codec.SalesforceConnectionCodec;
import com.nowellpoint.aws.api.model.sforce.Identity;
import com.nowellpoint.aws.api.model.sforce.Organization;
import com.nowellpoint.aws.data.annotation.Audited;
import com.nowellpoint.aws.data.annotation.Document;
import com.nowellpoint.aws.data.mongodb.MongoDocument;

@Audited
@Document(collectionName="salesforce.connectors", codec=SalesforceConnectionCodec.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class SalesforceConnector extends MongoDocument {

	/**
	 * 
	 */
	
	private static final long serialVersionUID = -3438714915624952119L;
	
	private User owner;
	
	private Identity identity;
	
	private Organization organization;
	
	private String tag;
	
	private Set<Environment> environments;
	
	private Set<ServiceInstance> serviceInstances;
	
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

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public Set<Environment> getEnvironments() {
		return environments;
	}

	public void setEnvironments(Set<Environment> environments) {
		this.environments = environments;
	}

	public Set<ServiceInstance> getServiceInstances() {
		return serviceInstances;
	}

	public void setServiceInstances(Set<ServiceInstance> serviceInstances) {
		this.serviceInstances = serviceInstances;
	}
}