package com.nowellpoint.console.entity;

import org.mongodb.morphia.annotations.Entity;

@Entity(value = "organizations")
public class Organization extends BaseEntity {
	
	private String number;
	
	private String name;
	
	private String domain;
	
	public Organization() {
		
	}

	public Organization(String id) {
		super(id);
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}
}