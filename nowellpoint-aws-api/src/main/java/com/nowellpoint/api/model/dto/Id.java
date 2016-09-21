package com.nowellpoint.api.model.dto;

public class Id {
	
	private String value;
	
	public Id(String value) {
		if (value == null) {
			throw new IllegalArgumentException("value for Id cannot be set to null");
		}
		this.value = value;
	}

	@Override
	public String toString() {
		return value;
	}
}