package com.nowellpoint.www.app.model;

import java.util.Set;

public class Service {
	
	private Double price;
	
	private String currencyIsoCode;
	
	private Boolean isActive;
	
	private String uom;
	
	private String type;
	
	private String name;
	
	private String description;
	
	private String configurationPage;
	
	private String company;
	
	private Set<String> configurationParams;
	
	public Service() {
		
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

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
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

	public Set<String> getConfigurationParams() {
		return configurationParams;
	}

	public void setConfigurationParams(Set<String> configurationParams) {
		this.configurationParams = configurationParams;
	}
}