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
	
	private Double price;
	
	private Boolean isActive;
	
	private String uom;
	
	private String authMethod;
	
	private String authUrl;
	
	private String displayName;
	
	private String description;
	
	private String image;
	
	private String localeSidKey;

	private String languageLocaleKey;
	
	private ServiceDetail serviceDetail;

	public ServiceProvider() {
		
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
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

	public String getAuthMethod() {
		return authMethod;
	}

	public void setAuthMethod(String authMethod) {
		this.authMethod = authMethod;
	}

	public String getAuthUrl() {
		return authUrl;
	}

	public void setAuthUrl(String authUrl) {
		this.authUrl = authUrl;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	public ServiceDetail getServiceDetail() {
		return serviceDetail;
	}

	public void setServiceDetail(ServiceDetail serviceDetail) {
		this.serviceDetail = serviceDetail;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getLocaleSidKey() {
		return localeSidKey;
	}

	public void setLocaleSidKey(String localeSidKey) {
		this.localeSidKey = localeSidKey;
	}

	public String getLanguageLocaleKey() {
		return languageLocaleKey;
	}

	public void setLanguageLocaleKey(String languageLocaleKey) {
		this.languageLocaleKey = languageLocaleKey;
	}
}