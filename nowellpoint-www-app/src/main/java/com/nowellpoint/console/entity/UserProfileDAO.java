package com.nowellpoint.console.entity;

import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.dao.BasicDAO;

public class UserProfileDAO extends BasicDAO<UserProfile, ObjectId>{

	public UserProfileDAO(Class<UserProfile> entityClass, Datastore ds) {
		super(entityClass, ds);
	}

}
