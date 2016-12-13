package com.nowellpoint.client.model;

public interface Result <T> {
	
	public Boolean isSuccess();
	
	public Integer getError();

	public String getErrorMessage();
	
	public T getTarget();
}