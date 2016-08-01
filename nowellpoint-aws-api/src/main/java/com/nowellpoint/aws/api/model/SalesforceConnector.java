package com.nowellpoint.aws.api.model;

import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.nowellpoint.aws.api.codec.SalesforceConnectionCodec;
import com.nowellpoint.aws.data.annotation.Audited;
import com.nowellpoint.aws.data.annotation.Document;
import com.nowellpoint.aws.data.mongodb.AbstractDocument;
import com.nowellpoint.aws.data.mongodb.sforce.Identity;
import com.nowellpoint.aws.data.mongodb.sforce.Organization;

@Audited
@Document(collectionName="salesforce.connectors", codec=SalesforceConnectionCodec.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class SalesforceConnector extends AbstractDocument {

	/**
	 * 
	 */
	
	private static final long serialVersionUID = -3438714915624952119L;
	
	private User owner;
	
	private Identity identity;
	
	private Organization organization;
	
	private String tag;
	
	private Set<Environment> environments;
	
	private List<ServiceInstance> serviceInstances;
	
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

	public List<ServiceInstance> getServiceInstances() {
		return serviceInstances;
	}

	public void setServiceInstances(List<ServiceInstance> serviceInstances) {
		this.serviceInstances = serviceInstances;
	}
}