package com.nowellpoint.console.entity;

import java.io.Serializable;

public class Feature implements Serializable {

	private static final long serialVersionUID = -6279402390046574460L;

	private Integer sortOrder;
	
	private String code;
	
	private String name;
	
	private Boolean enabled;
	
	private String description;
	
	private String quantity;
	
	public Feature() {
		
	}

	public Integer getSortOrder() {
		return sortOrder;
	}

	public void setSortOrder(Integer sortOrder) {
		this.sortOrder = sortOrder;
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

	public Boolean getEnabled() {
		return enabled;
	}

	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getQuantity() {
		return quantity;
	}

	public void setQuantity(String quantity) {
		this.quantity = quantity;
	}
}