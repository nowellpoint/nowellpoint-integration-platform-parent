package com.nowellpoint.console.service;

import java.util.ArrayList;
import java.util.List;

import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.QueryResults;

import com.nowellpoint.console.entity.PlanDAO;
import com.nowellpoint.console.model.Plan;

public class PlanService extends AbstractService {
	
	private PlanDAO planDAO;
	
	public PlanService() {
		planDAO = new PlanDAO(com.nowellpoint.console.entity.Plan.class, datastore);
	}

	public List<Plan> getPlans(String language) {
		
		Query<com.nowellpoint.console.entity.Plan> query = planDAO.createQuery()
				.field("language")
				.equal(language);
		
		QueryResults<com.nowellpoint.console.entity.Plan> queryResults = planDAO.find(query);
		
		List<com.nowellpoint.console.entity.Plan> documents = queryResults.asList();
		
		List<Plan> planList = new ArrayList<Plan>();
		
		if (! documents.isEmpty()) {
			documents.stream().forEach( d -> {
				planList.add(Plan.of(d));
			});
		}
		
		return planList;
	}
}