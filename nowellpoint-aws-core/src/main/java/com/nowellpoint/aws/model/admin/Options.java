package com.nowellpoint.aws.model.admin;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Options implements Serializable {

	private static final long serialVersionUID = 5068292559513580370L;
	
	private Salesforce salesforce;
	
	public Options() {
		
	}

	public Salesforce getSalesforce() {
		return salesforce;
	}

	public void setSalesforce(Salesforce salesforce) {
		this.salesforce = salesforce;
	}
}