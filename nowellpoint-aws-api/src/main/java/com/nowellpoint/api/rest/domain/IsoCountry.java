package com.nowellpoint.api.rest.domain;

import com.nowellpoint.mongodb.document.MongoDocument;

public class IsoCountry extends AbstractResource {
	
	private AbstractUserInfo createdBy;
	
	private AbstractUserInfo lastUpdatedBy;
	
	private String language;
	
	private String iso2Code;
	
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

	public AbstractUserInfo getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(AbstractUserInfo createdBy) {
		this.createdBy = createdBy;
	}

	public AbstractUserInfo getLastUpdatedBy() {
		return lastUpdatedBy;
	}

	public void setLastUpdatedBy(AbstractUserInfo lastUpdatedBy) {
		this.lastUpdatedBy = lastUpdatedBy;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
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