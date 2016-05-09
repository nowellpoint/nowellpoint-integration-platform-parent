package com.nowellpoint.client.sforce.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SObjectMapping {

    private String name;

    private String keyPrefix;

    private List<Field> fields;

    private String custom;
    
    private String label;

    private String labelPlural;

    private List<ChildRelationship> childRelationships;
    
    private List<RecordTypeInfo> recordTypeInfos;
   
    public SObjectMapping() {
    	
    }

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getKeyPrefix() {
		return keyPrefix;
	}

	public void setKeyPrefix(String keyPrefix) {
		this.keyPrefix = keyPrefix;
	}

	public List<Field> getFields() {
		return fields;
	}

	public void setFields(List<Field> fields) {
		this.fields = fields;
	}

	public String getCustom() {
		return custom;
	}

	public void setCustom(String custom) {
		this.custom = custom;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getLabelPlural() {
		return labelPlural;
	}

	public void setLabelPlural(String labelPlural) {
		this.labelPlural = labelPlural;
	}

	public List<ChildRelationship> getChildRelationships() {
		return childRelationships;
	}

	public void setChildRelationships(List<ChildRelationship> childRelationships) {
		this.childRelationships = childRelationships;
	}

	public List<RecordTypeInfo> getRecordTypeInfos() {
		return recordTypeInfos;
	}

	public void setRecordTypeInfos(List<RecordTypeInfo> recordTypeInfos) {
		this.recordTypeInfos = recordTypeInfos;
	}
}