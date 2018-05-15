package com.nowellpoint.console.service;

import java.util.Locale;

import org.bson.types.ObjectId;

import com.nowellpoint.console.entity.UserProfileDAO;
import com.nowellpoint.console.model.ModifiableUserProfile;
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
				.locale(new Locale(request.getLocale()))
				.phone(request.getPhone())
				.timeZone(request.getTimeZone())
				.title(request.getTitle())
				.build();
		
		com.nowellpoint.console.entity.UserProfile entity = modelMapper.map(userProfile, com.nowellpoint.console.entity.UserProfile.class);
		userProfileDAO.save(entity);
		return modelMapper.map(entity, ModifiableUserProfile.class).toImmutable();
	}
}