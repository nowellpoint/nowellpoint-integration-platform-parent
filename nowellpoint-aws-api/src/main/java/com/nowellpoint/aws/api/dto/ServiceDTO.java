package com.nowellpoint.aws.api.dto;

import java.util.Set;

public class ServiceDTO extends AbstractDTO {

	/**
	 * 
	 */
	
	private static final long serialVersionUID = 5553142927678572603L;
	
	private String code;
	
	private String name;
	
	private String description;
	
	private String company;

	private String configurationPage;
	
	private Set<String> configurationParams;
	
	public ServiceDTO() {
		
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
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

	public Set<String> getConfigurationParams() {
		return configurationParams;
	}

	public void setConfigurationParams(Set<String> configurationParams) {
		this.configurationParams = configurationParams;
	}
}