package com.nowellpoint.console.service;

import com.nowellpoint.console.model.Identity;
import com.nowellpoint.console.model.IdentityRequest;

public interface IdentityService {
	
	public Identity get(String id);
	
	public Identity getBySubject(String subject);
	
	public Identity create(IdentityRequest request);
	
	public Identity activate(String activationToken);
	
	public Identity setPassword(String id, String password);
	
	public Identity update(String id, IdentityRequest request);
	
	public Identity resendActivationEmail(String id);
	
	public Identity deactivate(String id);
	
	public void delete(String id);
}