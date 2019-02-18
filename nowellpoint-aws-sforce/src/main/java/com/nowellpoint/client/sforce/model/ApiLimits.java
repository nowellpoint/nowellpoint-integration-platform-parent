package com.nowellpoint.client.sforce.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ApiLimits extends Limit {
	
	private static final long serialVersionUID = -9197101738718518502L;

	@JsonProperty("Chatter Desktop")
	private Limit chatterDesktop;
	
	@JsonProperty("Chatter Mobile for BlackBerry")
	private Limit chatterMobileForBlackBerry;
	
	@JsonProperty("Sales Automation")
	private Limit salesAutomation;
	
	@JsonProperty("Salesforce Chatter")
	private Limit salesforceChatter;
	
	@JsonProperty("Salesforce Files")
	private Limit salesforceFiles;
	
	@JsonProperty("Salesforce for Android")
	private Limit salesforceForAndroid;
	
	@JsonProperty("Salesforce for iOS")
	private Limit salesforceForIOS;
	
	@JsonProperty("SalesforceA")
	private Limit salesforceA;
	
	public ApiLimits() {
		
	}

	public Limit getChatterMobileForBlackBerry() {
		return chatterMobileForBlackBerry;
	}

	public void setChatterMobileForBlackBerry(Limit chatterMobileForBlackBerry) {
		this.chatterMobileForBlackBerry = chatterMobileForBlackBerry;
	}

	public Limit getChatterDesktop() {
		return chatterDesktop;
	}

	public Limit getSalesAutomation() {
		return salesAutomation;
	}

	public Limit getSalesforceChatter() {
		return salesforceChatter;
	}

	public Limit getSalesforceFiles() {
		return salesforceFiles;
	}

	public Limit getSalesforceForAndroid() {
		return salesforceForAndroid;
	}

	public Limit getSalesforceForIOS() {
		return salesforceForIOS;
	}

	public Limit getSalesforceA() {
		return salesforceA;
	}
}