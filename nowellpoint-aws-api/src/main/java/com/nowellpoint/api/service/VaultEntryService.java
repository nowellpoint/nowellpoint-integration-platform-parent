package com.nowellpoint.api.service;

import java.util.List;

import com.nowellpoint.api.model.dynamodb.VaultEntry;

public interface VaultEntryService {
	
	public VaultEntry store(String key, String type, String value, String lastUpdatedBy);
	
	public VaultEntry replace(String token, String key, String type, String value, String lastUpdatedBy);
	
	public VaultEntry retrive(String token, String key);
	
	public void remove(String token);
	
	public List<VaultEntry> findByKey(String key);
}