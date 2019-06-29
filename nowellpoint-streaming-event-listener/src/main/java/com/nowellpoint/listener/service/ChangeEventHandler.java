package com.nowellpoint.listener.service;

import java.io.IOException;

import com.google.maps.errors.ApiException;
import com.nowellpoint.listener.model.ChangeEvent;
import com.nowellpoint.util.SecureValueException;

public interface ChangeEventHandler {
	public void handleChangeEvent(ChangeEvent event) throws SecureValueException, ApiException, InterruptedException, IOException;
}