package com.nowellpoint.listener.service;

import com.nowellpoint.listener.model.ChangeEvent;

public interface ChangeEventService {
	public void create(ChangeEvent changeEvent);
	public Long getReplayId(String organization);
}