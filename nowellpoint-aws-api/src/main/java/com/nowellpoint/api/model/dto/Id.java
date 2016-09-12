package com.nowellpoint.api.model.dto;

public class Id {
	
	private String value;
	
	public Id(String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return value;
	}
}