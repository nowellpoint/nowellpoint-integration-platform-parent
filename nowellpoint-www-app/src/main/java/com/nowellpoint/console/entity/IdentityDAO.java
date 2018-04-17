package com.nowellpoint.console.entity;

import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.dao.BasicDAO;

public class IdentityDAO extends BasicDAO<IdentityDocument, ObjectId>{

	public IdentityDAO(Class<IdentityDocument> entityClass, Datastore ds) {
		super(entityClass, ds);
	}
}