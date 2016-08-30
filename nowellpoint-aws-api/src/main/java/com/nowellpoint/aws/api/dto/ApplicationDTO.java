package com.nowellpoint.aws.api.dto;

import java.util.HashSet;
import java.util.Set;

public class ApplicationDTO extends AbstractDTO {

	/**
	 * 
	 */
	
	private static final long serialVersionUID = -365479731136738071L;
	
	private AccountProfileDTO owner;
	
	private String name;
	
	private String description;
	
	private Set<ServiceInstanceDTO> serviceInstances;
	
	private Set<EnvironmentDTO> environments;
	
	private String status;
	
	public ApplicationDTO() {
		
	}
	
	public ApplicationDTO(String id) {
		setId(id);
	}

	public AccountProfileDTO getOwner() {
		return owner;
	}

	public void setOwner(AccountProfileDTO owner) {
		this.owner = owner;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
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

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
}