package com.nowellpoint.listener.service;

import com.nowellpoint.listener.model.AccountEvent;

public interface AccountService {
	public void processEvent(AccountEvent event);
}