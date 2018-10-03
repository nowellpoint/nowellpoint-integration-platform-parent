package com.nowellpoint.console.entity;

import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.dao.BasicDAO;

public class DashboardDAO extends BasicDAO<Dashboard, ObjectId>{

	public DashboardDAO(Class<Dashboard> entityClass, Datastore ds) {
		super(entityClass, ds);
	}
}