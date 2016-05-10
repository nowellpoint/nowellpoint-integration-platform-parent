package com.nowellpoint.www.app.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ServiceInstance {
	
	private String key;
	
	private String name;
	
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
	
	private List<Environment> environments;
	
	private List<EnvironmentVariable> environmentVariables;
	
	private Map<String, List<EnvironmentVariableValue>> environmentVariableValues;
	
	private Long activeEnvironments;

	private String configurationPage;
	
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

	public String getDefaultEnvironment() {
		return defaultEnvironment;
	}

	public void setDefaultEnvironment(String defaultEnvironment) {
		this.defaultEnvironment = defaultEnvironment;
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
	
	public List<EnvironmentVariable> getEnvironmentVariables() {
		if (environmentVariables == null) {
			setEnvironmentVariables(new ArrayList<EnvironmentVariable>());
		} else {
			setEnvironmentVariables(environmentVariables
					.stream()
					.sorted((p1, p2) -> p1.getVariable().compareTo(p2.getVariable()))
					.collect(Collectors.toList()));
		}
		return environmentVariables;
	}

	public void setEnvironmentVariables(List<EnvironmentVariable> environmentVariables) {
		this.environmentVariables = environmentVariables;
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

	@JsonIgnore
	public Optional<Environment> getEnvironment(String name) {
		return getEnvironments()
				.stream()
				.filter(p -> p.getName().equals(name))
				.findFirst();
	}
}