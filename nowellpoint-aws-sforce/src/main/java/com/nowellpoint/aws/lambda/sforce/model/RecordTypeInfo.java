package com.nowellpoint.aws.lambda.sforce.model;

public class RecordTypeInfo {
	
    private String recordTypeId;

    private Urls urls;

    private String name;

    private String defaultRecordTypeMapping;

    private String available;

    private String master;
    
    public RecordTypeInfo() {
    	
    }

	public String getRecordTypeId() {
		return recordTypeId;
	}

	public void setRecordTypeId(String recordTypeId) {
		this.recordTypeId = recordTypeId;
	}

	public Urls getUrls() {
		return urls;
	}

	public void setUrls(Urls urls) {
		this.urls = urls;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDefaultRecordTypeMapping() {
		return defaultRecordTypeMapping;
	}

	public void setDefaultRecordTypeMapping(String defaultRecordTypeMapping) {
		this.defaultRecordTypeMapping = defaultRecordTypeMapping;
	}

	public String getAvailable() {
		return available;
	}

	public void setAvailable(String available) {
		this.available = available;
	}

	public String getMaster() {
		return master;
	}

	public void setMaster(String master) {
		this.master = master;
	}
}