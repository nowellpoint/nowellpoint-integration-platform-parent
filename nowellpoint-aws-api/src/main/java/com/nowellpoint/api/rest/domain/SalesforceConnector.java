package com.nowellpoint.api.rest.domain;

import java.util.HashSet;
import java.util.Set;

import com.nowellpoint.client.sforce.model.Identity;
import com.nowellpoint.client.sforce.model.Organization;
import com.nowellpoint.mongodb.document.MongoDocument;

public class SalesforceConnector extends AbstractResource {
	
	private String name;
	
	private UserInfo createdBy;
	
	private UserInfo lastUpdatedBy;
	
	private UserInfo owner;

	private Identity identity;
	
	private Organization organization;
	
	private String tag;
	
	private Set<Instance> instances;
	
	public SalesforceConnector() {
		
	}
	
	public SalesforceConnector(String id) {
		super(id);
	}
	
	public SalesforceConnector(MongoDocument document) {
		super(document);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public UserInfo getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(UserInfo createdBy) {
		this.createdBy = createdBy;
	}

	public UserInfo getLastUpdatedBy() {
		return lastUpdatedBy;
	}

	public void setLastUpdatedBy(UserInfo lastUpdatedBy) {
		this.lastUpdatedBy = lastUpdatedBy;
	}

	public UserInfo getOwner() {
		return owner;
	}

	public void setOwner(UserInfo owner) {
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

	public Set<Instance> getInstances() {
		return instances;
	}

	public void setInstances(Set<Instance> instances) {
		this.instances = instances;
	}
	
	public void addInstance(Instance instance) {
		if (instances == null || instances.isEmpty()) {
			instances = new HashSet<Instance>();
		}
		instances.add(instance);
	}
	
	@Override
	public MongoDocument toDocument() {
		return modelMapper.map(this, com.nowellpoint.api.model.document.SalesforceConnector.class);
	}
}