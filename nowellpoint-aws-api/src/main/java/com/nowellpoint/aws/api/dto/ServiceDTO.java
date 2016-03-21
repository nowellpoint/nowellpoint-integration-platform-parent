package com.nowellpoint.aws.api.dto;

import com.nowellpoint.aws.api.dto.sforce.ServiceInfo;

public class ServiceDTO extends AbstractDTO {

	/**
	 * 
	 */
	
	private static final long serialVersionUID = 5553142927678572603L;
	
	private String name;
	
	private String description;
	
	private String company;

	private String configurationPage;
	
	private ServiceInfo serviceInfo;
	
	public ServiceDTO() {
		
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getConfigurationPage() {
		return configurationPage;
	}

	public void setConfigurationPage(String configurationPage) {
		this.configurationPage = configurationPage;
	}

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public ServiceInfo getServiceInfo() {
		return serviceInfo;
	}

	public void setServiceInfo(ServiceInfo serviceInfo) {
		this.serviceInfo = serviceInfo;
	}
}