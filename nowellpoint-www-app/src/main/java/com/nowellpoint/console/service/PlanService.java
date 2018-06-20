package com.nowellpoint.console.service;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;

import org.bson.types.ObjectId;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.QueryResults;

import com.nowellpoint.console.entity.PlanDAO;
import com.nowellpoint.console.model.Plan;
import com.nowellpoint.util.Assert;

public class PlanService extends AbstractService {
	
	private PlanDAO planDAO;
	
	public PlanService() {
		planDAO = new PlanDAO(com.nowellpoint.console.entity.Plan.class, datastore);
	}
	
	public Plan get(String id) {
		com.nowellpoint.console.entity.Plan entity = null;
		try {
			entity = planDAO.get(new ObjectId(id));
		} catch (IllegalArgumentException e) {
			throw new BadRequestException(String.format("Invalid Plan Id: %s", id));
		}
		
		if (Assert.isNull(entity)) {
			throw new NotFoundException(String.format("Plan Id: %s was not found",id));
		}
		
		return Plan.of(entity);
	}

	public List<Plan> getPlans(String language) {
		
		Query<com.nowellpoint.console.entity.Plan> query = planDAO.createQuery()
				.field("language")
				.equal(language);
		
		QueryResults<com.nowellpoint.console.entity.Plan> queryResults = planDAO.find(query);
		
		List<com.nowellpoint.console.entity.Plan> entities = queryResults.asList();
		
		List<Plan> planList = new ArrayList<Plan>();
		
		if (! entities.isEmpty()) {
			entities.stream()
			.forEach( d -> {
				planList.add(Plan.of(d));
			});
		}
		
		return planList;
	}
}