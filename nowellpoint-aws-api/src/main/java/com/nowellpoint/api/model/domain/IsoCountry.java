package com.nowellpoint.api.model.domain;

import com.nowellpoint.mongodb.document.MongoDocument;

public class IsoCountry extends AbstractResource {
	
	private String language;
	
	private String code;
	
	private String name;
	
	private String description;
	
	public IsoCountry() {
		
	}
	
	public IsoCountry(String id) {
		super(id);
	}

	public IsoCountry(MongoDocument document) {
		super(document);
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

	@Override
	public MongoDocument toDocument() {
		return modelMapper.map(this, com.nowellpoint.api.model.document.IsoCountry.class);
	}	
}