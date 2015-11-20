package com.nowellpoint.aws.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Mapping {
	
	private String application;
	private String sourceType;
	private String collectionName;
	private List<FieldMappingEntry> fieldMappingEntries;
	
	public Mapping() {
		
	}

	public String getApplication() {
		return application;
	}

	public void setApplication(String application) {
		this.application = application;
	}

	public String getSourceType() {
		return sourceType;
	}

	public void setSourceType(String sourceType) {
		this.sourceType = sourceType;
	}

	public String getCollectionName() {
		return collectionName;
	}

	public void setCollectionName(String collectionName) {
		this.collectionName = collectionName;
	}

	public List<FieldMappingEntry> getFieldMappingEntries() {
		return fieldMappingEntries;
	}

	public void setFieldMappingEntries(List<FieldMappingEntry> fieldMappingEntries) {
		this.fieldMappingEntries = fieldMappingEntries;
	}
}