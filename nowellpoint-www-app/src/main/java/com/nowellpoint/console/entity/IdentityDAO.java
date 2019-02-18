package com.nowellpoint.console.entity;

import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.dao.BasicDAO;

public class IdentityDAO extends BasicDAO<Identity, ObjectId>{

	public IdentityDAO(Class<Identity> entityClass, Datastore ds) {
		super(entityClass, ds);
	}
}