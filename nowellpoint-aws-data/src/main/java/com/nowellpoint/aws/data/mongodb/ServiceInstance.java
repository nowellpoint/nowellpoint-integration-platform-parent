package com.nowellpoint.aws.data.mongodb;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.nowellpoint.aws.api.bus.ServiceInstanceMessageListener;
import com.nowellpoint.aws.data.ServiceInstanceCodec;
import com.nowellpoint.aws.data.annotation.MessageHandler;

@JsonIgnoreProperties(ignoreUnknown = true)
@MessageHandler(
		queueName="MONGODB_SERVICE_INSTANCE_COLLECTION_QUEUE", 
		collectionName="service.instances", 
		messageListener=ServiceInstanceMessageListener.class, 
		codec=ServiceInstanceCodec.class)
public class ServiceInstance extends AbstractDocument {

	/**
	 * 
	 */
	
	private static final long serialVersionUID = 3675473602093498225L;
	
	private String serviceProviderId;
	
	private String type;
	
	private Double price;
	
	private Boolean isActive;
	
	private String uom;
	
	private String configuration;
	
	private String account;
	
	private String instanceId;
	
	private String instanceName;
	
	private String instanceUrl;
	
	private Boolean isSandbox;
	
	private String name;
	
	private String description;

	public ServiceInstance() {
		
	}

	public String getServiceProviderId() {
		return serviceProviderId;
	}

	public void setServiceProviderId(String serviceProviderId) {
		this.serviceProviderId = serviceProviderId;
	}
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	public Boolean getIsActive() {
		return isActive;
	}

	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}

	public String getUom() {
		return uom;
	}

	public void setUom(String uom) {
		this.uom = uom;
	}

	public String getConfiguration() {
		return configuration;
	}

	public void setConfiguration(String configuration) {
		this.configuration = configuration;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
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

	public String getInstanceUrl() {
		return instanceUrl;
	}

	public void setInstanceUrl(String instanceUrl) {
		this.instanceUrl = instanceUrl;
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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}