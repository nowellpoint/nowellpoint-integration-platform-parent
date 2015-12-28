package com.nowellpoint.aws.model.sforce;

import java.io.Serializable;

import com.nowellpoint.aws.model.AbstractLambdaRequest;
import com.nowellpoint.aws.model.Lead;

public class CreateLeadRequest extends AbstractLambdaRequest implements Serializable {

	/**
	 * 
	 */
	
	private static final long serialVersionUID = -2911213185103305158L;
	
	private String accessToken;
	
	private String instanceUrl;
	
	private Lead lead;
	
	public CreateLeadRequest() {
		
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

	public Lead getLead() {
		return lead;
	}

	public void setLead(Lead lead) {
		this.lead = lead;
	}

	public CreateLeadRequest withAccessToken(String accessToken) {
		setAccessToken(accessToken);
		return this;
	}
	
	public CreateLeadRequest withInstanceUrl(String instanceUrl) {
		setInstanceUrl(instanceUrl);
		return this;
	}
	
	public CreateLeadRequest withLead(Lead lead) {
		setLead(lead);
		return this;
	}
}