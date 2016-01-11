package com.nowellpoint.aws.model.data;

import java.io.Serializable;

public class IsoCountry implements Serializable {

	private static final long serialVersionUID = 2884164327750192485L;
	
	private String id;
	
	private String language;
	
	private String code;
	
	private String name;
	
	private String description;

	public IsoCountry() {
		
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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
	
	public IsoCountry id(String id) {
		setId(id);
		return this;
	}
	
	public IsoCountry language(String language) {
		setLanguage(language);
		return this;
	}
	
	public IsoCountry code(String code) {
		setCode(code);
		return this;
	}
	
	public IsoCountry name(String name) {
		setName(name);
		return this;
	}
	
	public IsoCountry description(String description) {
		setDescription(description);
		return this;
	}
}