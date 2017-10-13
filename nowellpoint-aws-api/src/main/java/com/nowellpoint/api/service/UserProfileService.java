package com.nowellpoint.api.service;

import java.util.Locale;
import java.util.TimeZone;

import com.nowellpoint.api.rest.domain.Organization;
import com.nowellpoint.api.rest.domain.UserProfile;

public interface UserProfileService {
	public UserProfile findById(String id);
	public UserProfile findByUsername(String username);
	public UserProfile createUserProfile(String firstName, String lastName, String email, String phone, String countryCode, Organization organization);
	public UserProfile createUserProfile(String firstName, String lastName, String email, String phone, String countryCode, Organization organization, Locale locale, TimeZone timeZone);
	public UserProfile updateAddress(String id, String street, String city, String state, String postalCode, String countryCode);
	public UserProfile deactivateUserProfile(String id);
	public void deleteUserProfile(String id);
	public void setPassword(String id, String password);
	public void changePassword(String id, String oldPassword, String newPassword);
}