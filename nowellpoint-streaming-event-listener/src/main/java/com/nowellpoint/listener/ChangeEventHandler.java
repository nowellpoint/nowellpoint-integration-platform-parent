package com.nowellpoint.listener;

import java.io.IOException;

import com.google.maps.errors.ApiException;
import com.nowellpoint.client.sforce.model.changeevent.ChangeEvent;
import com.nowellpoint.util.SecureValueException;

public interface ChangeEventHandler {
	public void handleChangeEvent(ChangeEvent event, String organizationId, String refreshToken) throws SecureValueException, ApiException, InterruptedException, IOException;
}