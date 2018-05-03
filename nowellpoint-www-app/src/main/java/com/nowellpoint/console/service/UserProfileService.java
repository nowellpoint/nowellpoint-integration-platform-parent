package com.nowellpoint.console.service;

import org.bson.types.ObjectId;

import com.nowellpoint.console.entity.UserProfileDAO;
import com.nowellpoint.console.model.ModifiableUserProfile;
import com.nowellpoint.console.model.UserProfile;

public class UserProfileService extends AbstractService {
	
	private UserProfileDAO userProfileDAO;
	
	public UserProfileService() {
		userProfileDAO = new UserProfileDAO(com.nowellpoint.console.entity.UserProfile.class, datastore);
	}
	
	public UserProfile get(String id) {
		com.nowellpoint.console.entity.UserProfile document = userProfileDAO.get(new ObjectId(id));
		ModifiableUserProfile userProfile = modelMapper.map(document, ModifiableUserProfile.class);
		return userProfile.toImmutable();
	}
}