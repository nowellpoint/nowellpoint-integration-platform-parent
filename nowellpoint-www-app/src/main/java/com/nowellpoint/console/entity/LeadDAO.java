package com.nowellpoint.console.entity;

import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.dao.BasicDAO;

public class LeadDAO extends BasicDAO<Lead, ObjectId>{

	public LeadDAO(Class<Lead> entityClass, Datastore ds) {
		super(entityClass, ds);
	}
}