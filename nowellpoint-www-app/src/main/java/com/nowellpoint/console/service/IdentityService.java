package com.nowellpoint.console.service;

import com.nowellpoint.console.model.AddressRequest;
import com.nowellpoint.console.model.Identity;
import com.nowellpoint.console.model.IdentityRequest;
import com.nowellpoint.console.model.UserPreferenceRequest;
import com.nowellpoint.console.model.UserProfileRequest;

public interface IdentityService {
	
	public Identity get(String id);
	
	public Identity getBySubject(String subject);
	
	public Identity create(IdentityRequest request);
	
	public Identity activate(String activationToken);
	
	public Identity setPassword(String id, String password);
	
	public Identity update(String id, UserProfileRequest request);
	
	public Identity update(String id, AddressRequest request);
	
	public Identity update(String id, UserPreferenceRequest request);
	
	public Identity resendActivationEmail(String id);
	
	public Identity deactivate(String id);
	
	public void delete(String id);
}