package com.nowellpoint.console.entity;

import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.dao.BasicDAO;

public class PlanDAO extends BasicDAO<Plan, ObjectId>{

	public PlanDAO(Class<Plan> entityClass, Datastore ds) {
		super(entityClass, ds);
	}
}