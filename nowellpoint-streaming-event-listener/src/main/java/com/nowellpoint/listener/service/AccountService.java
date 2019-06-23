package com.nowellpoint.listener.service;

import java.io.IOException;

import com.google.maps.errors.ApiException;
import com.nowellpoint.client.sforce.model.changeevent.ChangeEvent;
import com.nowellpoint.listener.model.TopicConfiguration;
import com.nowellpoint.util.SecureValueException;

public interface AccountService {
	public void processChangeEvent(ChangeEvent event, TopicConfiguration configuration) throws SecureValueException, ApiException, InterruptedException, IOException;
}