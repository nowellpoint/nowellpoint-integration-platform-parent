package com.nowellpoint.client.model;

public interface CreateResult<T> extends Result {
	T getTarget();
}