package com.nowellpoint.aws.api.dto;

public class EnvironmentDTO {
	
	private Integer index;
	
	private String name;
	
	private String label;
	
	private Boolean active;
	
	public EnvironmentDTO() {

	}
	
	public Integer getIndex() {
		return index;
	}

	public void setIndex(Integer index) {
		this.index = index;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}
}