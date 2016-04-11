package com.nowellpoint.aws.api.dto;

import com.nowellpoint.aws.data.mongodb.ServiceInstance;

public class ApplicationDTO extends AbstractDTO {

	/**
	 * 
	 */
	
	private static final long serialVersionUID = -365479731136738071L;
	
	private AccountProfileDTO owner;
	
	private String name;
	
	private ServiceInstance serviceInstance;
	
	private String sourceId;
	
	private String sourceName;
	
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

	public ServiceInstance getServiceInstance() {
		return serviceInstance;
	}

	public void setServiceInstance(ServiceInstance serviceInstance) {
		this.serviceInstance = serviceInstance;
	}

	public String getSourceId() {
		return sourceId;
	}

	public void setSourceId(String sourceId) {
		this.sourceId = sourceId;
	}

	public String getSourceName() {
		return sourceName;
	}
	
	public void setSourceName(String sourceName) {
		this.sourceName = sourceName;
	}
}