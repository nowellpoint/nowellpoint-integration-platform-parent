package com.nowellpoint.client.model;

public interface Result {
	
	public Boolean isSuccess();
	
	public Integer getError();

	public String getErrorMessage();
}