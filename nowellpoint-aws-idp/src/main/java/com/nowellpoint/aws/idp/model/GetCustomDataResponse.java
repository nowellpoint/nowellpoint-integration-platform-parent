package com.nowellpoint.aws.idp.model;

import com.nowellpoint.aws.model.AbstractLambdaResponse;

public class GetCustomDataResponse extends AbstractLambdaResponse {

	/**
	 * 
	 */
	
	private static final long serialVersionUID = 7884867162394151341L;
	
	private CustomData customData;
	
	public GetCustomDataResponse() {
		
	}

	public CustomData getCustomData() {
		return customData;
	}

	public void setCustomData(CustomData customData) {
		this.customData = customData;
	}
}