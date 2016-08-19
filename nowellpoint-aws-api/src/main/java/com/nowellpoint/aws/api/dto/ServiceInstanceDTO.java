package com.nowellpoint.aws.api.dto;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

import com.nowellpoint.aws.api.model.EventListener;
import com.nowellpoint.aws.api.model.Plan;
import com.nowellpoint.aws.api.model.Targets;

public class ServiceInstanceDTO implements Serializable {
	
	private static final long serialVersionUID = 1L;

	private String key;
	
	private Date addedOn;
	
	private Date updatedOn;
	
	private String name;
	
	private String providerType;
	
	private Boolean isActive;
	
	private String providerName;
	
	private String serviceType;
	
	private String serviceName;
	
	private String tag;
	
	private String sourceEnvironment;
	
	private String status;
	
	private Set<EventListener> eventListeners;
	
	private Targets targets;

	private String configurationPage;
	
	private Plan plan;

	public ServiceInstanceDTO() {
		
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public Date getAddedOn() {
		return addedOn;
	}

	public void setAddedOn(Date addedOn) {
		this.addedOn = addedOn;
	}

	public Date getUpdatedOn() {
		return updatedOn;
	}

	public void setUpdatedOn(Date updatedOn) {
		this.updatedOn = updatedOn;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getProviderType() {
		return providerType;
	}

	public void setProviderType(String providerType) {
		this.providerType = providerType;
	}

	public Boolean getIsActive() {
		return isActive;
	}

	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}

	public String getProviderName() {
		return providerName;
	}

	public void setProviderName(String providerName) {
		this.providerName = providerName;
	}

	public String getServiceType() {
		return serviceType;
	}

	public void setServiceType(String serviceType) {
		this.serviceType = serviceType;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public String getSourceEnvironment() {
		return sourceEnvironment;
	}

	public void setSourceEnvironment(String sourceEnvironment) {
		this.sourceEnvironment = sourceEnvironment;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Set<EventListener> getEventListeners() {
		return eventListeners;
	}

	public void setEventListeners(Set<EventListener> eventListeners) {
		this.eventListeners = eventListeners;
	}

	public Targets getTargets() {
		return targets;
	}

	public void setTargets(Targets targets) {
		this.targets = targets;
	}

	public String getConfigurationPage() {
		return configurationPage;
	}

	public void setConfigurationPage(String configurationPage) {
		this.configurationPage = configurationPage;
	}

	public Plan getPlan() {
		return plan;
	}

	public void setPlan(Plan plan) {
		this.plan = plan;
	}
}