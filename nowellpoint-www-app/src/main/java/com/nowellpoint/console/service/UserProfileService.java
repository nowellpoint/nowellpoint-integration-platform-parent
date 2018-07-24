package com.nowellpoint.console.service;

import com.nowellpoint.console.model.UserAddressRequest;
import com.nowellpoint.console.model.UserPreferenceRequest;
import com.nowellpoint.console.model.UserProfile;
import com.nowellpoint.console.model.UserProfileRequest;

public interface UserProfileService {
		
	public UserProfile get(String id);
	
	public UserProfile create(UserProfileRequest request);
	
	public UserProfile update(String id, UserProfileRequest request);
	
	public UserProfile update(String id, UserAddressRequest request);
	
	public UserProfile update(String id, UserPreferenceRequest request);
}