package com.nowellpoint.aws.api.dto;

import java.util.HashSet;
import java.util.Set;

import com.nowellpoint.client.sforce.model.Identity;
import com.nowellpoint.client.sforce.model.Organization;

public class SalesforceConnectorDTO extends AbstractDTO {

	private static final long serialVersionUID = -6847034908687287362L;
	
	private AccountProfileDTO owner;

	private Identity identity;
	
	private Organization organization;
	
	private String tag;
	
	private Set<EnvironmentDTO> environments;
	
	private Set<ServiceInstanceDTO> serviceInstances;
	
	public SalesforceConnectorDTO() {
		
	}
	
	public SalesforceConnectorDTO(String id) {
		setId(id);
	}

	public AccountProfileDTO getOwner() {
		return owner;
	}

	public void setOwner(AccountProfileDTO owner) {
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

	public Set<EnvironmentDTO> getEnvironments() {
		return environments;
	}

	public void setEnvironments(Set<EnvironmentDTO> environments) {
		this.environments = environments;
	}
	
	public void addEnvironment(EnvironmentDTO environment) {
		if (environments == null || environments.isEmpty()) {
			environments = new HashSet<EnvironmentDTO>();
		}
		environments.add(environment);
	}

	public Set<ServiceInstanceDTO> getServiceInstances() {
		return serviceInstances;
	}

	public void setServiceInstances(Set<ServiceInstanceDTO> serviceInstances) {
		this.serviceInstances = serviceInstances;
	}
	
	public void addServiceInstance(ServiceInstanceDTO serviceInstance) {
		if (serviceInstances == null || serviceInstances.isEmpty()) {
			serviceInstances = new HashSet<ServiceInstanceDTO>();
		}
		serviceInstances.add(serviceInstance);
	}
}