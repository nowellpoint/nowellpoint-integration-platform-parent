package com.nowellpoint.sdk.salesforce.model;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement( name = "Package" )
public class Package {
	
	private String fullName;
	
	private Double version;
	
	private List<Type> types;
	
	public Package() {
		
	}
	
	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public Double getVersion() {
		return version;
	}
	
	public void setVersion(Double version) {
		this.version = version;
	}

	public List<Type> getTypes() {
		return types;
	}

	public void setTypes(List<Type> types) {
		this.types = types;
	}
}