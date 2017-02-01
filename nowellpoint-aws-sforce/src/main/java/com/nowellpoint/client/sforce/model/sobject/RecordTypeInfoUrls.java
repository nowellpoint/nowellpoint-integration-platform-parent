package com.nowellpoint.client.sforce.model.sobject;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RecordTypeInfoUrls implements Serializable {

	private static final long serialVersionUID = 1592500440339469657L;
	
	private String layout;
	
	public RecordTypeInfoUrls() {
		
	}

	public String getLayout() {
		return layout;
	}

	public void setLayout(String layout) {
		this.layout = layout;
	}
}