package com.nowellpoint.client.model;

public interface UpdateResult<T> extends Result {
	T getTarget();
}