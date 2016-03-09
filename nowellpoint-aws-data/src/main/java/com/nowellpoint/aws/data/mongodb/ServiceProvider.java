package com.nowellpoint.aws.data.mongodb;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.nowellpoint.aws.api.bus.ServiceProviderMessageListener;
import com.nowellpoint.aws.data.ServiceProviderCodec;
import com.nowellpoint.aws.data.annotation.MessageHandler;

@JsonIgnoreProperties(ignoreUnknown = true)
@MessageHandler(
		queueName="MONGODB_SERVICE_PROVIDER_COLLECTION_QUEUE", 
		collectionName="service.providers", 
		messageListener=ServiceProviderMessageListener.class, 
		codec=ServiceProviderCodec.class)

public class ServiceProvider extends AbstractDocument implements Serializable {

	/**
	 * 
	 */
	
	private static final long serialVersionUID = 8096961003246656184L;
	
	private String type;
	
	private String key;
	
	private String instanceName;
	
	private String instanceUrl;
	
	private String account;
	
	private Boolean isActive;
	
	private Double price;
	
	private User owner;
	
	private String organization;
	
	public ServiceProvider() {
		
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

	public User getOwner() {
		return owner;
	}

	public void setOwner(User owner) {
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