package com.nowellpoint.content.model;

public class Feature {
	
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
	public String getCode() {
		return code;
	}
	public String getName() {
		return name;
	}
	public Boolean getEnabled() {
		return enabled;
	}
	public String getDescription() {
		return description;
	}
	public String getQuantity() {
		return quantity;
	}
}