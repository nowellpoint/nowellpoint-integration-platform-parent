package com.nowellpoint.aws.client;

import com.nowellpoint.aws.model.ClientException;
import com.nowellpoint.aws.model.sforce.GetAuthorizationRequest;
import com.nowellpoint.aws.model.sforce.GetAuthorizationResponse;
import com.nowellpoint.aws.model.sforce.GetTokenRequest;
import com.nowellpoint.aws.model.sforce.GetTokenResponse;
import com.nowellpoint.aws.model.sforce.RevokeTokenRequest;
import com.nowellpoint.aws.model.sforce.RevokeTokenResponse;
import com.nowellpoint.aws.model.sforce.CreateSObjectRequest;
import com.nowellpoint.aws.model.sforce.CreateSObjectResponse;
import com.nowellpoint.aws.model.sforce.CreateLeadRequest;
import com.nowellpoint.aws.model.sforce.CreateLeadResponse;

public class SalesforceClient extends AbstractClient {
	
	public SalesforceClient() {
		
	}
	
	public GetTokenResponse authenticate(GetTokenRequest getTokenRequest) throws ClientException {
		return invoke("SalesforceAuthenticationRequest", getTokenRequest, GetTokenResponse.class);
	}

	public GetAuthorizationResponse authorize(GetAuthorizationRequest authorizationRequest) throws ClientException {
		return invoke("SalesforceTokenRequest", authorizationRequest, GetAuthorizationResponse.class);
	}
	
	public RevokeTokenResponse revoke(RevokeTokenRequest revokeTokenRequest) throws ClientException {
		return invoke("SalesforceRevokeTokenRequest", revokeTokenRequest, RevokeTokenResponse.class);
	}
	
	public CreateSObjectResponse createSObject(CreateSObjectRequest createSObjectRequest) throws ClientException {
		return invoke("SalesforceCreateSObject", createSObjectRequest, CreateSObjectResponse.class);
	}
	
	public CreateLeadResponse createLead(CreateLeadRequest createLeadRequest) throws ClientException {
		return invoke("SalesforceCreateLead", createLeadRequest, CreateLeadResponse.class);
	}
}