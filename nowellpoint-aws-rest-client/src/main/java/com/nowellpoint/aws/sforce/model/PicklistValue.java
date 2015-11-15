package com.nowellpoint.aws.sforce.model;

public class PicklistValue {
	
	private String value;

    private String validFor;

    private String active;

    private String label;

    private String defaultValue;
    
    public PicklistValue() {
    	
    }

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getValidFor() {
		return validFor;
	}

	public void setValidFor(String validFor) {
		this.validFor = validFor;
	}

	public String getActive() {
		return active;
	}

	public void setActive(String active) {
		this.active = active;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}
}