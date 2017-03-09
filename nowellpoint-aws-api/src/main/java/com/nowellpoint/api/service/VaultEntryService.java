package com.nowellpoint.api.service;

import java.util.List;

import com.nowellpoint.api.model.dynamodb.VaultEntry;

public interface VaultEntryService {
	
	public VaultEntry store(String value);
	
	public VaultEntry replace(String token, String value);
	
	public VaultEntry retrive(String token);
	
	public void remove(String token);
	
	public List<VaultEntry> findByKey(String key);
}