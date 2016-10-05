package com.nowellpoint.client.model;

public interface Result <T> {
	
	public Boolean getIsSuccess();
	
	public Integer getError();

	public String getErrorMessage();
	
	public T getTarget();
}