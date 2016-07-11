package com.nowellpoint.aws.api.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.nowellpoint.aws.api.codec.IsoCountryCodec;
import com.nowellpoint.aws.data.annotation.Document;
import com.nowellpoint.aws.data.mongodb.AbstractDocument;

@JsonIgnoreProperties(ignoreUnknown = true)
@Document(collectionName="iso.countries", codec=IsoCountryCodec.class)
public class IsoCountry extends AbstractDocument {

	private static final long serialVersionUID = 2884164327750192485L;
	
	private String language;
	
	private String code;
	
	private String name;
	
	private String description;

	public IsoCountry() {
		
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