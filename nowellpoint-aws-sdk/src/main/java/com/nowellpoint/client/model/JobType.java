package com.nowellpoint.client.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class JobType extends AbstractResource {
	
	private String name;
	
	private String code;
	
	private String description;
	
	private String languageSidKey;
	
	private ConnectorType source;
	
	private ConnectorType target;
	
	public JobType() {
		
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getLanguageSidKey() {
		return languageSidKey;
	}

	public void setLanguageSidKey(String languageSidKey) {
		this.languageSidKey = languageSidKey;
	}

	public ConnectorType getSource() {
		return source;
	}

	public void setSource(ConnectorType source) {
		this.source = source;
	}

	public ConnectorType getTarget() {
		return target;
	}

	public void setTarget(ConnectorType target) {
		this.target = target;
	}
}