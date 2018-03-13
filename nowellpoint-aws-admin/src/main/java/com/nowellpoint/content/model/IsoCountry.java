package com.nowellpoint.content.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class IsoCountry {
	private String code;
	
	private String name;
	
	public IsoCountry() {
		
	}
	
	public String getCode() {
		return code;
	}
	
	public String getName() {
		return name;
	}
}