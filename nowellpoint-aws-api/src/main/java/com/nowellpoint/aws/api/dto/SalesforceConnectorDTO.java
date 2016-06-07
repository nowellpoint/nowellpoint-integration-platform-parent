package com.nowellpoint.aws.api.dto;

import java.util.List;

import com.nowellpoint.aws.api.model.ServiceInstance;
import com.nowellpoint.client.sforce.model.Identity;
import com.nowellpoint.client.sforce.model.Organization;

public class SalesforceConnectorDTO extends AbstractDTO {

	private static final long serialVersionUID = -6847034908687287362L;
	
	private AccountProfileDTO owner;

	private Identity identity;
	
	private Organization organization;
	
	private String tag;
	
	private List<ServiceInstance> serviceInstances;
	
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

	public List<ServiceInstance> getServiceInstances() {
		return serviceInstances;
	}

	public void setServiceInstances(List<ServiceInstance> serviceInstances) {
		this.serviceInstances = serviceInstances;
	}
	
	public void addServiceInstance(ServiceInstance serviceInstance) {
		serviceInstances.add(serviceInstance);
	}
}