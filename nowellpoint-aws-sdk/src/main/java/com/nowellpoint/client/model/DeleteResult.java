package com.nowellpoint.client.model;

public interface DeleteResult extends Result {
	
	public Boolean isSuccess();
	
	public String getError();

	public String getErrorMessage();

}