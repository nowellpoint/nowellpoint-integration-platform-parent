package com.nowellpoint.aws.api.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.nowellpoint.aws.api.codec.SalesforceConnectionCodec;
import com.nowellpoint.aws.data.annotation.Audited;
import com.nowellpoint.aws.data.annotation.Document;
import com.nowellpoint.aws.data.mongodb.AbstractDocument;
import com.nowellpoint.aws.data.mongodb.sforce.Identity;
import com.nowellpoint.aws.data.mongodb.sforce.Organization;
import com.sforce.soap.partner.DescribeGlobalSObjectResult;

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
	
	private List<ServiceInstance> serviceInstances;
	
	private DescribeGlobalSObjectResult[] sobjects;
	
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

	public List<ServiceInstance> getServiceInstances() {
		return serviceInstances;
	}

	public void setServiceInstances(List<ServiceInstance> serviceInstances) {
		this.serviceInstances = serviceInstances;
	}

	public DescribeGlobalSObjectResult[] getSobjects() {
		return sobjects;
	}

	public void setSobjects(DescribeGlobalSObjectResult[] sobjects) {
		this.sobjects = sobjects;
	}
}