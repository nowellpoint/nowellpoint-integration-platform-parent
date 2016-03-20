package com.nowellpoint.aws.data.mongodb;

import java.io.Serializable;

public class ServiceDetail implements Serializable {

	/**
	 * 
	 */
	
	private static final long serialVersionUID = 1537820218475752902L;
	
	private String name;
	
	private String description;
	
	private String company;

	private String configurationPage;
	
	public ServiceDetail() {
		
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
}