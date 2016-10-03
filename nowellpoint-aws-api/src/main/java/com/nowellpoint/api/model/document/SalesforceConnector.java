package com.nowellpoint.api.model.document;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.nowellpoint.api.model.codec.SalesforceConnectionCodec;
import com.nowellpoint.api.model.sforce.Identity;
import com.nowellpoint.api.model.sforce.Organization;
import com.nowellpoint.mongodb.annotation.Document;
import com.nowellpoint.mongodb.document.MongoDocument;

@Document(collectionName="salesforce.connectors", codec=SalesforceConnectionCodec.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class SalesforceConnector extends MongoDocument {

	/**
	 * 
	 */
	
	private static final long serialVersionUID = -3438714915624952119L;
	
	private UserRef createdBy;
	
	private UserRef lastModifiedBy;
	
	private UserRef owner;
	
	private Identity identity;
	
	private Organization organization;
	
	private String tag;
	
	private Set<Environment> environments;
	
	private Set<ServiceInstance> serviceInstances;
	
	public SalesforceConnector() {
		
	}

	public UserRef getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(UserRef createdBy) {
		this.createdBy = createdBy;
	}

	public UserRef getLastModifiedBy() {
		return lastModifiedBy;
	}

	public void setLastModifiedBy(UserRef lastModifiedBy) {
		this.lastModifiedBy = lastModifiedBy;
	}

	public UserRef getOwner() {
		return owner;
	}

	public void setOwner(UserRef owner) {
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