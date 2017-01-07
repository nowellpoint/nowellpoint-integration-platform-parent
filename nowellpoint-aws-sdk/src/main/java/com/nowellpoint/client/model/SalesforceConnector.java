package com.nowellpoint.client.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.nowellpoint.client.model.sforce.Identity;
import com.nowellpoint.client.model.sforce.Organization;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SalesforceConnector extends AbstractResource {
	
	private String name;

	private Identity identity;
	
	private Organization organization;
	
	private UserInfo owner;
	
	private String tag;
	
	private List<Instance> instances;
	
	public SalesforceConnector() {
		setEnvironments(Collections.emptyList());
	}
	
	public SalesforceConnector(String id) {
		setId(id);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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

	public UserInfo getOwner() {
		return owner;
	}

	public void setOwner(UserInfo owner) {
		this.owner = owner;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public List<Instance> getEnvironments() {
		if (instances == null) {
			setEnvironments(new ArrayList<Instance>());
		}
		return instances;
	}

	public void setEnvironments(List<Instance> instances) {
		this.instances = instances;
	}
}