package com.nowellpoint.www.app.model;

public class ServiceProviderInstance extends BaseEntity {
	
	/**
	 * 
	 */
	
	private static final long serialVersionUID = 9100844172085644173L;

	private String id;
	
	private String type;
	
	private String key;
	
	private Identity owner;
	
	private String organization;
	
	private String instanceName;
	
	private String instanceUrl;
	
	private String instanceId;
	
	private String account;
	
	private Boolean isActive;
	
	private Double price;
	
	public ServiceProviderInstance() {
		
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public Identity getOwner() {
		return owner;
	}

	public void setOwner(Identity owner) {
		this.owner = owner;
	}

	public String getOrganization() {
		return organization;
	}

	public void setOrganization(String organization) {
		this.organization = organization;
	}

	public String getInstanceName() {
		return instanceName;
	}

	public void setInstanceName(String instanceName) {
		this.instanceName = instanceName;
	}

	public String getInstanceUrl() {
		return instanceUrl;
	}

	public void setInstanceUrl(String instanceUrl) {
		this.instanceUrl = instanceUrl;
	}

	public String getInstanceId() {
		return instanceId;
	}

	public void setInstanceId(String instanceId) {
		this.instanceId = instanceId;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public Boolean getIsActive() {
		return isActive;
	}

	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}
}