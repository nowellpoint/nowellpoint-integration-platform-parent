package com.nowellpoint.signup.entity;

import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.dao.BasicDAO;

public class RegistrationDAO extends BasicDAO<RegistrationDocument, ObjectId> {

	public RegistrationDAO(Class<RegistrationDocument> entityClass, Datastore datastore) {
		super(entityClass, datastore);
	}
}