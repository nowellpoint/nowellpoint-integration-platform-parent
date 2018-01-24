package com.nowellpoint.api.rest.impl;

import javax.inject.Inject;
import javax.ws.rs.core.Response;

import com.nowellpoint.api.rest.UserProfileResource;
import com.nowellpoint.api.rest.domain.AddressRequest;
import com.nowellpoint.api.rest.domain.UserProfile;
import com.nowellpoint.api.rest.domain.UserProfileRequest;
import com.nowellpoint.api.service.UserProfileService;

public class UserProfileResourceImpl implements UserProfileResource {
	
	@Inject
	private UserProfileService userProfileService;

	@Override
	public Response getUserProfile(String id) {
		UserProfile userProfile = userProfileService.findById(id);
		return Response.ok(userProfile)
				.build();
	}

	@Override
	public Response updateAddress(String id, AddressRequest request) {
		UserProfile userProfile = userProfileService.updateAddress(id, request);
		return Response.ok(userProfile)
				.build();
	}

	@Override
	public Response createUserProfile(String firstName, String lastName, String company, String division,
			String department, String title, String email, String mobilePhone, String phone, String extension,
			String locale, String timeZone) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Response udpateUserProfile(String id, UserProfileRequest request) {
		UserProfile userProfile = userProfileService.updateUserProfile(id, request);
		return Response.ok(userProfile)
				.build();
	}

	@Override
	public Response deactivateUserProfile(String id) {
		userProfileService.deactivateUserProfile(id);
		return Response.ok()
				.build();
	}
}