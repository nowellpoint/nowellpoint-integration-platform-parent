package com.nowellpoint.aws.api.model;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ServiceInstance implements Serializable {

	/**
	 * 
	 */
	
	private static final long serialVersionUID = 3675473602093498225L;
	
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
	
	private Set<Environment> environments;
	
	private Map<String, Set<EnvironmentVariableValue>> environmentVariableValues;
	
	private Long activeEnvironments;
	
	private Set<EventListener> eventListeners;
	
	private Targets targets;

	private String configurationPage;
	
	private Plan plan;
	
	public ServiceInstance() {
		
	}
	
	public String getKey() {
		return key;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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
	
	public String getConfigurationPage() {
		return configurationPage;
	}

	public void setConfigurationPage(String configurationPage) {
		this.configurationPage = configurationPage;
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

	public Set<Environment> getEnvironments() {
		return environments;
	}

	public void setEnvironments(Set<Environment> environments) {
		this.environments = environments;
	}

	public void addEnvironment(String name, Boolean active) {
		environments.add(new Environment(name, active));
	}
	
	public Map<String,Set<EnvironmentVariableValue>> getEnvironmentVariableValues() {
		return environmentVariableValues;
	}
	
	public Long getActiveEnvironments() {
		return activeEnvironments;
	}

	public void setActiveEnvironments(Long activeEnvironments) {
		this.activeEnvironments = activeEnvironments;
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

	public Plan getPlan() {
		return plan;
	}

	public void setPlan(Plan plan) {
		this.plan = plan;
	}

	public void setEnvironmentVariableValues(Map<String, Set<EnvironmentVariableValue>> environmentVariableValues) {
//		environments.stream().forEach(environment -> {
//			if (environment.getEnvironmentVariables() == null) {
//				environment.setEnvironmentVariables(new HashSet<EnvironmentVariable>());
//			}
//			environment.getEnvironmentVariables().stream().forEach(variable -> {
//				if (environmentVariableValues.containsKey(variable.getVariable())) {
//					if (variable.getEnvironmentVariableValues() == null) {
//						
//					}
//					variable.setEnvironmentVariableValues(new HashSet<EnvironmentVariableValue>());
//					variable.getEnvironmentVariableValues().addAll(environmentVariableValues.get(variable.getVariable()));
//				}
//			});
//		});
		this.environmentVariableValues = environmentVariableValues;
	}
}