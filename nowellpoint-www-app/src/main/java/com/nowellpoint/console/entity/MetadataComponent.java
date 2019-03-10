package com.nowellpoint.console.entity;

import java.io.Serializable;

public class MetadataComponent implements Serializable {

	private static final long serialVersionUID = -608912014418788980L;
	private String unit;
	private Double value;
	private Double delta;

	public MetadataComponent() {
		
	}

	public String getUnit() {
		return unit;
	}

	public Double getValue() {
		return value;
	}

	public Double getDelta() {
		return delta;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public void setValue(Double value) {
		this.value = value;
	}

	public void setDelta(Double delta) {
		this.delta = delta;
	}
}