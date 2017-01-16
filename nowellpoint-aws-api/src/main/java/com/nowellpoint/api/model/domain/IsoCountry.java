package com.nowellpoint.api.model.domain;

public class IsoCountry extends AbstractResource {
	
	private String language;
	
	private String code;
	
	private String name;
	
	private String description;
	
	public IsoCountry() {
		
	}

	public IsoCountry(com.nowellpoint.api.model.document.IsoCountry document) {
		modelMapper.map(document, IsoCountry.class);
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}	
}