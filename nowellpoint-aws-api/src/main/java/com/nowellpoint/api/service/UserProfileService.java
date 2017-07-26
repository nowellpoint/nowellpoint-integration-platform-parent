package com.nowellpoint.api.service;

import com.nowellpoint.api.rest.domain.UserProfile;

public interface UserProfileService {
	
	public UserProfile findByUsername(String username);
	
	public UserProfile createUserProfile(String userId, String firstName, String lastName, String email, String countryCode);

}