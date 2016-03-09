package com.nowellpoint.aws.api.dto;

public class ServiceProviderDTO extends AbstractDTO {

	/**
	 * 
	 */
	
	private static final long serialVersionUID = 5553142927678572603L;
	
	private String type;
	
	private String key;
	
	private String instanceName;
	
	private String instanceUrl;
	
	private String account;
	
	private Boolean isActive;
	
	private Double price;
	
	private String organization;
	
	private IdentityDTO owner;
	
	public ServiceProviderDTO() {
		
	}
	
	public ServiceProviderDTO(String id) {
		setId(id);
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

	public IdentityDTO getOwner() {
		return owner;
	}

	public void setOwner(IdentityDTO owner) {
		this.owner = owner;
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

	public String getOrganization() {
		return organization;
	}

	public void setOrganization(String organization) {
		this.organization = organization;
	}	
}