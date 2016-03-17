package com.nowellpoint.aws.api.dto;

import java.io.Serializable;

public class ServiceProviderDTO implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1297944724385795255L;
	
	private String type;
	
	private String instanceId;
	
	private String instanceName;
	
	private Boolean isSandbox;
		
	private String name;
		
	private String instanceUrl;
	
	private String account;
	
	private Double price;

	public ServiceProviderDTO() {
		
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getInstanceId() {
		return instanceId;
	}

	public void setInstanceId(String instanceId) {
		this.instanceId = instanceId;
	}

	public String getInstanceName() {
		return instanceName;
	}

	public void setInstanceName(String instanceName) {
		this.instanceName = instanceName;
	}

	public Boolean getIsSandbox() {
		return isSandbox;
	}

	public void setIsSandbox(Boolean isSandbox) {
		this.isSandbox = isSandbox;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getInstanceUrl() {
		return instanceUrl;
	}

	public void setInstanceUrl(String instanceUrl) {
		this.instanceUrl = instanceUrl;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}
	
	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}
}