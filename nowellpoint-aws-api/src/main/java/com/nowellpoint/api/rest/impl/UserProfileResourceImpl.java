package com.nowellpoint.api.rest.impl;

import javax.inject.Inject;
import javax.ws.rs.core.Response;

import com.nowellpoint.api.rest.UserProfileResource;
import com.nowellpoint.api.rest.domain.UserProfile;
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
	public Response updateAddress(String id, String city, String countryCode, String postalCode, String state,
			String street) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Response createUserProfile(String firstName, String lastName, String company, String division,
			String department, String title, String email, String mobilePhone, String phone, String extension,
			String locale, String timeZone) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Response udpateUserProfile(String id, String firstName, String lastName, String company, String division,
			String department, String title, String email, String mobilePhone, String phone, String extension,
			String locale, String timeZone) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Response deactivateUserProfile(String id) {
		// TODO Auto-generated method stub
		return null;
	}
}