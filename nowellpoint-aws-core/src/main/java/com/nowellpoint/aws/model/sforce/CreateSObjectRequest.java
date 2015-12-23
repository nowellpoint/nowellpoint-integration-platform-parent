package com.nowellpoint.aws.model.sforce;

import java.io.Serializable;

import com.nowellpoint.aws.model.AbstractLambdaRequest;

public class CreateSObjectRequest extends AbstractLambdaRequest implements Serializable {

	/**
	 * 
	 */
	
	private static final long serialVersionUID = -2911213185103305158L;
	
	private String accessToken;
	
	private String instanceUrl;
	
	private String sObject;
	
	private String type;
	
	public CreateSObjectRequest() {
		
	}

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public String getInstanceUrl() {
		return instanceUrl;
	}

	public void setInstanceUrl(String instanceUrl) {
		this.instanceUrl = instanceUrl;
	}

	public String getSObject() {
		return sObject;
	}

	public void setSObject(String sObject) {
		this.sObject = sObject;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public CreateSObjectRequest withAccessToken(String accessToken) {
		setAccessToken(accessToken);
		return this;
	}
	
	public CreateSObjectRequest withInstanceUrl(String instanceUrl) {
		setInstanceUrl(instanceUrl);
		return this;
	}
	
	public CreateSObjectRequest withSObject(String sobject) {
		setSObject(sobject);
		return this;
	}
	
	public CreateSObjectRequest withType(String type) {
		setType(type);
		return this;
	}
}