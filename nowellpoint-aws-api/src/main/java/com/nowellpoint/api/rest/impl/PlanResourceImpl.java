package com.nowellpoint.api.rest.impl;

import javax.inject.Inject;
import javax.ws.rs.core.Response;

import com.nowellpoint.api.rest.PlanResource;
import com.nowellpoint.api.rest.PlanService;
import com.nowellpoint.api.rest.domain.Plan;
import com.nowellpoint.api.rest.domain.PlanList;

public class PlanResourceImpl implements PlanResource {
	
	@Inject
	private PlanService planService;
	
	public Response getAllActive(String localeSidKey, String languageSidKey) {
		
		PlanList resources = planService.getAllActive(localeSidKey, languageSidKey);
		
		return Response.ok(resources)
				.build();
	}
	
	public Response findById(String id) {
		
		Plan plan = planService.findById(id);
		
		return Response.ok(plan)
				.build();
		
	}
}