package com.nowellpoint.aws.api.dto;

public class ApplicationDTO extends AbstractDTO {

	/**
	 * 
	 */
	
	private static final long serialVersionUID = -365479731136738071L;
	
	private String name;
	
	private ServiceInstanceDTO serviceInstance;
	
	private String sourceId;
	
	private String sourceName;
	
	public ApplicationDTO() {
		
	}
	
	public ApplicationDTO(String id) {
		setId(id);
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public ServiceInstanceDTO getServiceInstance() {
		return serviceInstance;
	}

	public void setServiceInstance(ServiceInstanceDTO serviceInstance) {
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