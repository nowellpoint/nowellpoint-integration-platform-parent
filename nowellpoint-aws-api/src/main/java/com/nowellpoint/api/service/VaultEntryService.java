package com.nowellpoint.api.service;

import java.util.List;

import com.nowellpoint.api.model.dynamodb.VaultEntry;

public interface VaultEntryService {
	
	public VaultEntry store(String value);
	
	public VaultEntry store(String key, String value);
	
	public VaultEntry replace(String key, String value);
	
	public VaultEntry retrive(String key);
	
	public void remove(String key);
	
	public List<VaultEntry> findByKey(String key);
}