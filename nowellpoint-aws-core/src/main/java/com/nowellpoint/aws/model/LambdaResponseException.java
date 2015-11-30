package com.nowellpoint.aws.model;

import java.io.IOException;

public class LambdaResponseException extends RuntimeException {

	/**
	 * 
	 */

	private static final long serialVersionUID = -1428665704644815400L;
	
	public LambdaResponseException(IOException e) {
		super(e);
	}
}