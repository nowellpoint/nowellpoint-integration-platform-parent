package com.nowellpoint.client.model;

public interface Result {
	
	public Boolean isSuccess();
	
	public String getError();

	public String getErrorMessage();
}