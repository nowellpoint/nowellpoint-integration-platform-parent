package com.nowellpoint.api.rest.service;

import java.time.Instant;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import com.nowellpoint.api.rest.domain.Address;
import com.nowellpoint.api.rest.domain.ReferenceLink;
import com.nowellpoint.api.rest.domain.ReferenceLinkTypes;
import com.nowellpoint.api.rest.domain.UserInfo;
import com.nowellpoint.api.rest.domain.UserProfile;
import com.nowellpoint.api.service.UserProfileService;
import com.nowellpoint.api.util.CountryProvider;
import com.nowellpoint.api.util.UserContext;

public class UserProfileServiceImpl extends AbstractUserProfileService implements UserProfileService {

	@Override
	public UserProfile findByUsername(String username) {
		return super.findByUsername(username);
	}

	@Override
	public UserProfile createUserProfile(String userId, String firstName, String lastName, String email, String countryCode) {
		String country = getCountry(countryCode);
		
		UserInfo userInfo = UserInfo.of(UserContext.getPrincipal().getName());
		
		ReferenceLink referenceLink = ReferenceLink.of(ReferenceLinkTypes.OKTA, userId);
		
		Date now = Date.from(Instant.now());
		
		Address address = Address.builder()
				.countryCode(countryCode)
				.country(country)
				.build();
		
		UserProfile userProfile = UserProfile.builder()
				.firstName(firstName)
				.lastName(lastName)
				.email(email)
				.username(email)
				.address(address)
				.locale(Locale.getDefault())
				.timeZone(TimeZone.getDefault())
				.isActive(Boolean.FALSE)
				.createdBy(userInfo)
				.createdOn(now)
				.lastUpdatedBy(userInfo)
				.lastUpdatedOn(now)
				.addReferenceLink(referenceLink)
				.build();
		
		create(userProfile);
		
		return userProfile;
	}
	
	private String getCountry(String countryCode) {
		return CountryProvider.getCountry(Locale.getDefault(), countryCode);
	}
}