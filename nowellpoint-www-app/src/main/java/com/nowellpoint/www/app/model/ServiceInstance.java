package com.nowellpoint.www.app.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ServiceInstance {
	
	private String key;
	
	private String name;
	
	private String providerType;
	
	private Boolean isActive;
	
	private String providerName;
	
	private String serviceType;
	
	private String serviceName;
	
	private String tag;
	
	private String sourceEnvironment;
	
	private String status;
	
	private List<Environment> environments;
	
	private Map<String, List<EnvironmentVariableValue>> environmentVariableValues;
	
	private Long activeEnvironments;
	
	private List<EventListener> eventListeners;
	
	private Targets targets;

	private String configurationPage;
	
	private Plan plan;
	
	public ServiceInstance() {

	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
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

	public String getConfigurationPage() {
		return configurationPage;
	}

	public void setConfigurationPage(String configurationPage) {
		this.configurationPage = configurationPage;
	}
	
	public List<Environment> getEnvironments() {
		if (environments == null) {
			setEnvironments(new ArrayList<Environment>());
		}
		return environments;
	}

	public void setEnvironments(List<Environment> environments) {
		this.environments = environments;
	}
	
	public Map<String,List<EnvironmentVariableValue>> getEnvironmentVariableValues() {
		return environmentVariableValues;
	}

	public void setEnvironmentVariableValues(Map<String, List<EnvironmentVariableValue>> environmentVariableValues) {
		this.environmentVariableValues = environmentVariableValues;
	}
	
	public Long getActiveEnvironments() {
		return activeEnvironments;
	}

	public void setActiveEnvironments(Long activeEnvironments) {
		this.activeEnvironments = activeEnvironments;
	}

	public List<EventListener> getEventListeners() {
		return eventListeners;
	}

	public void setEventListeners(List<EventListener> eventListeners) {
		this.eventListeners = eventListeners;
	}

	public Targets getTargets() {
		return targets;
	}

	public void setTargets(Targets targets) {
		this.targets = targets;
	}

	public Plan getPlan() {
		return plan;
	}

	public void setPlan(Plan plan) {
		this.plan = plan;
	}

	@JsonIgnore
	public Optional<Environment> getEnvironment(String name) {
		return getEnvironments()
				.stream()
				.filter(p -> p.getName().equals(name))
				.findFirst();
	}
}