package com.nowellpoint.console.service;

import java.sql.Date;
import java.time.Instant;
import java.util.Locale;

import org.bson.types.ObjectId;

import com.nowellpoint.console.entity.UserProfileDAO;
import com.nowellpoint.console.model.Address;
import com.nowellpoint.console.model.ModifiableUserProfile;
import com.nowellpoint.console.model.UserAddressRequest;
import com.nowellpoint.console.model.UserProfile;
import com.nowellpoint.console.model.UserProfileRequest;

public class UserProfileService extends AbstractService {
	
	private UserProfileDAO userProfileDAO;
	
	public UserProfileService() {
		userProfileDAO = new UserProfileDAO(com.nowellpoint.console.entity.UserProfile.class, datastore);
	}
	
	public UserProfile get(String id) {
		com.nowellpoint.console.entity.UserProfile entity = userProfileDAO.get(new ObjectId(id));
		ModifiableUserProfile userProfile = modelMapper.map(entity, ModifiableUserProfile.class);
		return userProfile.toImmutable();
	}
	
	public UserProfile update(String id, UserProfileRequest request) {
		UserProfile instance = get(id);
		
		UserProfile userProfile = UserProfile.builder()
				.from(instance)
				.email(request.getEmail())
				.firstName(request.getFirstName())
				.lastName(request.getLastName())
				.name(request.getFirstName() != null || ! request.getFirstName().isEmpty() ? request.getFirstName().concat(" ").concat(request.getLastName()) : request.getLastName())
				.locale(convertStringToLocale(request.getLocale()))
				.phone(request.getPhone())
				.timeZone(request.getTimeZone())
				.title(request.getTitle())
				.build();
		
		return update(userProfile);
	}
	
	public UserProfile update(String id, UserAddressRequest request) {
		UserProfile instance = get(id);
		
		Address address = Address.builder()
				.from(instance.getAddress())
				.city(request.getCity())
				.countryCode(request.getCountryCode())
				.postalCode(request.getPostalCode())
				.state(request.getState())
				.street(request.getStreet())
				.updatedOn(Date.from(Instant.now()))
				.build();
		
		UserProfile userProfile = UserProfile.builder()
				.from(instance)
				.address(address)
				.build();
		
		return update(userProfile);
		
	}
	
	private UserProfile update(UserProfile userProfile) {
		com.nowellpoint.console.entity.UserProfile entity = modelMapper.map(userProfile, com.nowellpoint.console.entity.UserProfile.class);
		userProfileDAO.save(entity);
		return modelMapper.map(entity, ModifiableUserProfile.class).toImmutable();
	}
	
	private Locale convertStringToLocale(String localeString) {
		String[] tokens = localeString.split("_");
		if (tokens.length == 1) {
			return new Locale(tokens[0]);
		} else if (tokens.length == 2) {
			return new Locale(tokens[0], tokens[1]);
		} else if (tokens.length == 3) {
			return new Locale(tokens[0], tokens[1], tokens[2]);
		} else {
			return null;
		}
	}
}