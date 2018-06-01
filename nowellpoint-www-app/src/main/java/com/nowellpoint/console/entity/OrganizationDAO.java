package com.nowellpoint.console.entity;

import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.dao.BasicDAO;

public class OrganizationDAO extends BasicDAO<Organization, ObjectId> {

	public OrganizationDAO(Class<Organization> entityClass, Datastore ds) {
		super(entityClass, ds);
	}
}