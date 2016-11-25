package com.nowellpoint.api.model.domain;

import java.util.HashSet;
import java.util.Set;

import com.nowellpoint.client.sforce.model.Identity;
import com.nowellpoint.client.sforce.model.Organization;

public class SalesforceConnector extends AbstractResource {

	private static final long serialVersionUID = -6847034908687287362L;
	
	public String name;
	
	private UserInfo createdBy;
	
	private UserInfo lastModifiedBy;
	
	private UserInfo owner;

	private Identity identity;
	
	private Organization organization;
	
	private String tag;
	
	private Set<Environment> environments;
	
	public SalesforceConnector() {
		
	}
	
	public SalesforceConnector(String id) {
		super(id);
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

	public UserInfo getLastModifiedBy() {
		return lastModifiedBy;
	}

	public void setLastModifiedBy(UserInfo lastModifiedBy) {
		this.lastModifiedBy = lastModifiedBy;
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

	public Set<Environment> getEnvironments() {
		return environments;
	}

	public void setEnvironments(Set<Environment> environments) {
		this.environments = environments;
	}
	
	public void addEnvironment(Environment environment) {
		if (environments == null || environments.isEmpty()) {
			environments = new HashSet<Environment>();
		}
		environments.add(environment);
	}
}