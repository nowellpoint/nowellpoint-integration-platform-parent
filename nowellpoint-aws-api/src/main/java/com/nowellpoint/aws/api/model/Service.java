package com.nowellpoint.aws.api.model;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

public class Service implements Serializable {

	/**
	 * 
	 */
	
	private static final long serialVersionUID = 1537820218475752902L;
	
	private Double price;
	
	private String currencyIsoCode;
	
	private Boolean isActive;
	
	private Integer quantity;
	
	private String unitOfMeasure;
	
	private String type;
	
	private String serviceName;
	
	private String description;
	
	private String company;
	
	private Integer sandboxCount;

	private String configurationPage;
	
	private Set<EnvironmentVariable> environmentVariables;
	
	private Map<String, Set<EnvironmentVariableValue>> environmentVariableValues;
	
	private Set<Plan> plans;
	
	public Service() {
		
	}

	public Boolean getIsActive() {
		return isActive;
	}

	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public String getCurrencyIsoCode() {
		return currencyIsoCode;
	}

	public void setCurrencyIsoCode(String currencyIsoCode) {
		this.currencyIsoCode = currencyIsoCode;
	}

	public String getUnitOfMeasure() {
		return unitOfMeasure;
	}

	public void setUnitOfMeasure(String unitOfMeasure) {
		this.unitOfMeasure = unitOfMeasure;
	}
	
	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getConfigurationPage() {
		return configurationPage;
	}

	public void setConfigurationPage(String configurationPage) {
		this.configurationPage = configurationPage;
	}

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public Integer getSandboxCount() {
		return sandboxCount;
	}

	public void setSandboxCount(Integer sandboxCount) {
		this.sandboxCount = sandboxCount;
	}
	
	public Set<EnvironmentVariable> getEnvironmentVariables() {
		return environmentVariables;
	}

	public void setEnvironmentVariables(Set<EnvironmentVariable> environmentVariables) {
		this.environmentVariables = environmentVariables;
	}

	public Map<String, Set<EnvironmentVariableValue>> getEnvironmentVariableValues() {
		return environmentVariableValues;
	}

	public void setEnvironmentVariableValues(Map<String, Set<EnvironmentVariableValue>> environmentVariableValues) {
		this.environmentVariableValues = environmentVariableValues;
	}

	public Set<Plan> getPlans() {
		return plans;
	}

	public void setPlans(Set<Plan> plans) {
		this.plans = plans;
	}
}