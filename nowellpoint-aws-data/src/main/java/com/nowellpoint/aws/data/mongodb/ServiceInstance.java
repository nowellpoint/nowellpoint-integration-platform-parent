package com.nowellpoint.aws.data.mongodb;

import java.io.Serializable;
import java.util.HashSet;
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
	
	private String providerType;
	
	private Boolean isActive;
	
	private String providerName;
	
	private Double price;
	
	private String currencyIsoCode;
	
	private String uom;
	
	private String serviceType;
	
	private String serviceName;
	
	private String tag;
	
	private String defaultEnvironment;
	
	private Set<Environment> environments;
	
	private Set<EnvironmentVariable> environmentVariables;
	
	private Map<String, Set<EnvironmentVariableValue>> environmentVariableValues;
	
	private Long activeEnvironments;

	private String configurationPage;
	
	public ServiceInstance() {
		
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

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	public String getCurrencyIsoCode() {
		return currencyIsoCode;
	}

	public void setCurrencyIsoCode(String currencyIsoCode) {
		this.currencyIsoCode = currencyIsoCode;
	}

	public String getUom() {
		return uom;
	}

	public void setUom(String uom) {
		this.uom = uom;
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
	
	public String getDefaultEnvironment() {
		return defaultEnvironment;
	}

	public void setDefaultEnvironment(String defaultEnvironment) {
		this.defaultEnvironment = defaultEnvironment;
	}

	public Set<Environment> getEnvironments() {
		return environments;
	}

	public void setEnvironments(Set<Environment> environments) {
		this.environments = environments;
		setActiveEnvironments(this.environments.stream().filter(environment -> environment.getActive()).count());
	}

	public Set<EnvironmentVariable> getEnvironmentVariables() {
		return environmentVariables;
	}

	public void setEnvironmentVariables(Set<EnvironmentVariable> environmentVariables) {
		this.environmentVariables = environmentVariables;
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

	public void setEnvironmentVariableValues(Map<String, Set<EnvironmentVariableValue>> environmentVariableValues) {
		environmentVariables.stream().forEach(variable -> {
			if (environmentVariableValues.containsKey(variable.getVariable())) {
				variable.setEnvironmentVariableValues(new HashSet<EnvironmentVariableValue>());
				variable.getEnvironmentVariableValues().addAll(environmentVariableValues.get(variable.getVariable()));
			}
		});
		environments.stream().forEach(environment -> {
			if (environment.getEnvironmentVariables() == null) {
				environment.setEnvironmentVariables(new HashSet<EnvironmentVariable>());
			}
			environment.getEnvironmentVariables().stream().forEach(variable -> {
				if (environmentVariableValues.containsKey(variable.getVariable())) {
					if (variable.getEnvironmentVariableValues() == null) {
						
					}
					variable.setEnvironmentVariableValues(new HashSet<EnvironmentVariableValue>());
					variable.getEnvironmentVariableValues().addAll(environmentVariableValues.get(variable.getVariable()));
				}
			});
		});
		this.environmentVariableValues = environmentVariableValues;
	}
}