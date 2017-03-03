package com.nowellpoint.api.rest.domain;

import com.nowellpoint.mongodb.document.MongoDocument;

public class IsoCountry extends AbstractResource {
	
	private UserInfo createdBy;
	
	private UserInfo lastUpdatedBy;
	
	private String language;
	
	private String code;
	
	private String name;
	
	private String description;
	
	public IsoCountry() {
		
	}
	
	private <T> IsoCountry(T document) {
		modelMapper.map(document, this);
	}
	
	public static IsoCountry of(MongoDocument document) {
		return new IsoCountry(document);
	}

	public UserInfo getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(UserInfo createdBy) {
		this.createdBy = createdBy;
	}

	public UserInfo getLastUpdatedBy() {
		return lastUpdatedBy;
	}

	public void setLastUpdatedBy(UserInfo lastUpdatedBy) {
		this.lastUpdatedBy = lastUpdatedBy;
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