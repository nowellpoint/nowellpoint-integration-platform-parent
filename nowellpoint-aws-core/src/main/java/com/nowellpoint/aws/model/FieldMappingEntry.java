package com.nowellpoint.aws.model;

public class FieldMappingEntry {
	
	private String source;
	private Boolean custom;
	private Boolean mapped;
	private Destination destination;
	
	public FieldMappingEntry() {
		
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public Boolean getCustom() {
		return custom;
	}

	public void setCustom(Boolean custom) {
		this.custom = custom;
	}

	public Boolean getMapped() {
		return mapped;
	}

	public void setMapped(Boolean mapped) {
		this.mapped = mapped;
	}

	public Destination getDestination() {
		return destination;
	}

	public void setDestination(Destination destination) {
		this.destination = destination;
	}
}