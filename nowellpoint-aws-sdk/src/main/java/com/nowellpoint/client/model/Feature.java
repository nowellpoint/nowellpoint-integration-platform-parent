package com.nowellpoint.client.model;

public class Feature {
	
	private Integer sortOrder;
	
	private String code;
	
	private String name;
	
	private String description;
	
	private Boolean enabled;
	
	private String quantity;
	
	public Feature() {
		
	}

	public Integer getSortOrder() {
		return sortOrder;
	}

	public String getCode() {
		return code;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public Boolean getEnabled() {
		return enabled;
	}

	public String getQuantity() {
		return quantity;
	}
}