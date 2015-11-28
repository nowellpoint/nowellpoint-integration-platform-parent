package com.nowellpoint.aws.model.sforce;

import com.nowellpoint.aws.model.AbstractLambdaResponse;

public class GetIdentityResponse extends AbstractLambdaResponse {

	/**
	 * 
	 */
	
	private static final long serialVersionUID = -4249532606207243086L;
	
	private Identity identity;
	
	public GetIdentityResponse() {
		
	}

	public Identity getIdentity() {
		return identity;
	}

	public void setIdentity(Identity identity) {
		this.identity = identity;
	}
}