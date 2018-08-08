package com.nowellpoint.console.service.impl;

import java.util.Locale;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;

import org.bson.types.ObjectId;

import com.nowellpoint.console.entity.UserProfileDAO;
import com.nowellpoint.console.model.Address;
import com.nowellpoint.console.model.Preferences;
import com.nowellpoint.console.model.UserAddressRequest;
import com.nowellpoint.console.model.UserPreferenceRequest;
import com.nowellpoint.console.model.UserProfile;
import com.nowellpoint.console.model.UserProfileRequest;
import com.nowellpoint.console.service.AbstractService;
import com.nowellpoint.console.service.UserProfileService;
import com.nowellpoint.console.util.UserContext;
import com.nowellpoint.util.Assert;

public class UserProfileServiceImpl extends AbstractService implements UserProfileService {
	
	private UserProfileDAO userProfileDAO;
	
	public UserProfileServiceImpl() {
		userProfileDAO = new UserProfileDAO(com.nowellpoint.console.entity.UserProfile.class, datastore);
	}
	
	@Override
	public UserProfile get(String id) {
		com.nowellpoint.console.entity.UserProfile entity = null;
		try {
			entity = userProfileDAO.get(new ObjectId(id));
		} catch (IllegalArgumentException e) {
			throw new BadRequestException(String.format("Invalid UserProfile Id: %s", id));
		}
		
		if (Assert.isNull(entity)) {
			throw new NotFoundException(String.format("UserProfile Id: %s was not found",id));
		}
		
		return UserProfile.of(entity);
	}
	
	@Override
	public UserProfile create(UserProfileRequest request) {
		
		UserProfile userProfile = UserProfile.builder()
				.email(request.getEmail())
				.firstName(request.getFirstName())
				.lastName(request.getLastName())
				.name(request.getFirstName() != null || ! request.getFirstName().isEmpty() ? request.getFirstName().concat(" ").concat(request.getLastName()) : request.getLastName())
				.phone(request.getPhone())
				.title(request.getTitle())
				.build();
		
		return create(userProfile);
	}
	
	@Override
	public UserProfile update(String id, UserProfileRequest request) {
		UserProfile instance = get(id);
		
		UserProfile userProfile = UserProfile.builder()
				.from(instance)
				.email(request.getEmail())
				.firstName(request.getFirstName())
				.lastName(request.getLastName())
				.name(request.getFirstName() != null || ! request.getFirstName().isEmpty() ? request.getFirstName().concat(" ").concat(request.getLastName()) : request.getLastName())
				.phone(request.getPhone())
				.title(request.getTitle())
				.build();
		
		return update(userProfile);
	}
	
	@Override
	public UserProfile update(String id, UserAddressRequest request) {
		UserProfile instance = get(id);
		
		Address address = Address.builder()
				.from(instance.getAddress())
				.city(request.getCity())
				.countryCode(request.getCountryCode())
				.postalCode(request.getPostalCode())
				.state(request.getState())
				.street(request.getStreet())
				.updatedOn(getCurrentDateTime())
				.build();
		
		UserProfile userProfile = UserProfile.builder()
				.from(instance)
				.address(address)
				.build();
		
		return update(userProfile);
	}
	
	@Override
	public UserProfile update(String id, UserPreferenceRequest request) {
		UserProfile instance = get(id);
		
		Preferences preferences = Preferences.builder()
				.from(instance.getPreferences())
				.locale(convertStringToLocale(request.getLocale()))
				.timeZone(request.getTimeZone())
				.build();
		
		UserProfile userProfile = UserProfile.builder()
				.from(instance)
				.preferences(preferences)
				.build();
		
		return update(userProfile);
	}
	
	private UserProfile create(UserProfile userProfile) {
		com.nowellpoint.console.entity.UserProfile entity = modelMapper.map(userProfile, com.nowellpoint.console.entity.UserProfile.class);
		entity.setCreatedOn(getCurrentDateTime());
		entity.setCreatedBy(UserContext.get().getId());
		entity.setLastUpdatedOn(entity.getCreatedOn());
		entity.setLastUpdatedBy(entity.getCreatedBy());
		userProfileDAO.save(entity);
		return UserProfile.of(entity);
	}
	
	private UserProfile update(UserProfile userProfile) {
		com.nowellpoint.console.entity.UserProfile entity = modelMapper.map(userProfile, com.nowellpoint.console.entity.UserProfile.class);
		entity.setLastUpdatedOn(getCurrentDateTime());
		entity.setLastUpdatedBy(UserContext.get().getId());
		userProfileDAO.save(entity);
		return UserProfile.of(entity);
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