package com.nowellpoint.www.app.model;

public class ServiceProvider {

	private String type;
	
	private String instanceId;
	
	private String instanceName;
	
	private Boolean isSandbox;
		
	private String name;
		
	private String instanceUrl;
	
	private String account;
	
	public ServiceProvider() {
		
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
}