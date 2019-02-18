package com.nowellpoint.sdk.salesforce.model.sobject;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RecordTypeInfo implements Serializable {
	
	private static final long serialVersionUID = -9027607808560048930L;

	private String recordTypeId;

    private String name;

    private Boolean defaultRecordTypeMapping;

    private Boolean available;

    private Boolean master;
    
    private RecordTypeInfoUrls urls;
    
    public RecordTypeInfo() {
    	
    }

	public String getRecordTypeId() {
		return recordTypeId;
	}

	public void setRecordTypeId(String recordTypeId) {
		this.recordTypeId = recordTypeId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Boolean getDefaultRecordTypeMapping() {
		return defaultRecordTypeMapping;
	}

	public void setDefaultRecordTypeMapping(Boolean defaultRecordTypeMapping) {
		this.defaultRecordTypeMapping = defaultRecordTypeMapping;
	}

	public Boolean getAvailable() {
		return available;
	}

	public void setAvailable(Boolean available) {
		this.available = available;
	}

	public Boolean getMaster() {
		return master;
	}

	public void setMaster(Boolean master) {
		this.master = master;
	}

	public RecordTypeInfoUrls getUrls() {
		return urls;
	}

	public void setUrls(RecordTypeInfoUrls urls) {
		this.urls = urls;
	}
}