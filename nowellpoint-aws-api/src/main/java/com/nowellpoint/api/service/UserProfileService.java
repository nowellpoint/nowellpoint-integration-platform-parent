package com.nowellpoint.api.service;

import java.util.Locale;
import java.util.TimeZone;

import com.nowellpoint.api.rest.domain.UserProfile;

public interface UserProfileService {
	public UserProfile findById(String id);
	public UserProfile findByUsername(String username);
	public UserProfile findByReferenceId(String referenceId);
	public UserProfile createUserProfile(String firstName, String lastName, String email, String countryCode);
	public UserProfile createUserProfile(String firstName, String lastName, String email, String countryCode, Locale locale, TimeZone timeZone);
	public UserProfile updateAddress(String id, String street, String city, String state, String postalCode, String countryCode);
	public UserProfile deactivateUserProfile(String id);
}