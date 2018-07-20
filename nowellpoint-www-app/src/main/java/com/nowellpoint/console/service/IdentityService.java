package com.nowellpoint.console.service;

import com.nowellpoint.console.model.Identity;

public interface IdentityService {
	
	public Identity getIdentity(String id);
	
	public Identity getBySubject(String subject);
}