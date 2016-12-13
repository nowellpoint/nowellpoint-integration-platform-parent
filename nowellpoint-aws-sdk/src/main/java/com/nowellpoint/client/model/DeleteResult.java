package com.nowellpoint.client.model;

public interface DeleteResult {
	
	public Boolean isSuccess();
	
	public Integer getError();

	public String getErrorMessage();

}