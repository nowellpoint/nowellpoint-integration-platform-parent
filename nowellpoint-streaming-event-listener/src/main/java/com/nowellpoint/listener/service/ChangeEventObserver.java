package com.nowellpoint.listener.service;

import com.nowellpoint.listener.model.AccountEvent;

public interface ChangeEventObserver {
	public void eventObserver(AccountEvent event);
}