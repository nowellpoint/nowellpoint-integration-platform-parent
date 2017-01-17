package com.nowellpoint.client.model;

public interface DeleteResult extends Result {
	
	public Boolean isSuccess();
	
	public Integer getError();

	public String getErrorMessage();

}