package com.nowellpoint.aws.idp.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.nowellpoint.aws.model.AbstractResponse;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RevokeTokenResponse extends AbstractResponse implements Serializable {
	
	private static final long serialVersionUID = -7030159892953022982L;

	public RevokeTokenResponse() {
		
	}
}