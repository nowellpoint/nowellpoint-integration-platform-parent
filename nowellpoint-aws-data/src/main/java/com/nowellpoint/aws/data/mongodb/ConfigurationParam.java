package com.nowellpoint.aws.data.mongodb;

import java.io.Serializable;

public class ConfigurationParam implements Serializable {

	/**
	 * 
	 */
	
	private static final long serialVersionUID = -465329639025346275L;
	
	private String name;
	
	private String value;
	
	public ConfigurationParam() {
		
	}
	
	public ConfigurationParam(String name, String value) {
		setName(name);
		setValue(value);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}