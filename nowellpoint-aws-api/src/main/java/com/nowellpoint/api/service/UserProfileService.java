package com.nowellpoint.api.service;

import java.util.Locale;
import java.util.Set;
import java.util.TimeZone;

import com.nowellpoint.api.rest.domain.AddressRequest;
import com.nowellpoint.api.rest.domain.Organization;
import com.nowellpoint.api.rest.domain.UserProfile;
import com.nowellpoint.api.rest.domain.UserProfileRequest;

public interface UserProfileService {
	public UserProfile findById(String id);
	public UserProfile findByReferenceId(String referenceId);
	public UserProfile findByUsername(String username);
	public Set<UserProfile> queryByOrganizationId(String organizationId);
	public UserProfile createUserProfile(String firstName, String lastName, String email, String phone, String countryCode, Organization organization);
	public UserProfile createUserProfile(String firstName, String lastName, String email, String phone, String countryCode, Organization organization, Locale locale, TimeZone timeZone);
	public UserProfile updateUserProfile(String id, UserProfileRequest request);
	public UserProfile updateAddress(String id, AddressRequest request);
	public UserProfile deactivateUserProfile(String id);
	public void deleteUserProfile(String id);
	public void setPassword(String id, String password);
	public void changePassword(String id, String oldPassword, String newPassword);
}