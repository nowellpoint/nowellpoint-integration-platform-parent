package com.nowellpoint.client.model;

public class IsoCountry extends AbstractResource {
	
	private String iso2Code;
	
	private String name;
	
	public IsoCountry() {
		
	}

	public String getIso2Code() {
		return iso2Code;
	}

	public void setIso2Code(String iso2Code) {
		this.iso2Code = iso2Code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}