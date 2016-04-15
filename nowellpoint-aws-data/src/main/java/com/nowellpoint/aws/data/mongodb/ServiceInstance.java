package com.nowellpoint.aws.data.mongodb;

import java.io.Serializable;
import java.util.HashSet;
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

	private String configurationPage;
	
	private Set<ConfigurationParam> configurationParams;
	
	public ServiceInstance() {
		configurationParams = new HashSet<ConfigurationParam>();
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

	public Set<ConfigurationParam> getConfigurationParams() {
		return configurationParams;
	}

	public void setConfigurationParams(Set<ConfigurationParam> configurationParams) {
		this.configurationParams = configurationParams;
	}
	
	public void addConfigurationParam(ConfigurationParam configurationParam) {
		this.configurationParams.add(configurationParam);
	}
}