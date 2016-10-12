package com.nowellpoint.api.model.dto;

import java.util.Set;

public class Service {
	
	private String code;
	
	private String name;
	
	private Set<Feature> features; 
	
	public Service() {
		
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Set<Feature> getFeatures() {
		return features;
	}

	public void setFeatures(Set<Feature> features) {
		this.features = features;
	}

}