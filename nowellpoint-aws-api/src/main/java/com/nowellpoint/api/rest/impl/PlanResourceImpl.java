package com.nowellpoint.api.rest.impl;

import javax.inject.Inject;
import javax.ws.rs.core.Response;

import com.nowellpoint.api.rest.PlanResource;
import com.nowellpoint.api.rest.domain.Plan;
import com.nowellpoint.api.rest.domain.PlanList;
import com.nowellpoint.api.service.PlanService;

public class PlanResourceImpl implements PlanResource {
	
	@Inject
	private PlanService planService;
	
	public Response getAllActive(String locale, String language) {
		
		PlanList resources = planService.getAllActive(locale, language);
		
		return Response.ok(resources)
				.build();
	}
	
	public Response findById(String id) {
		
		Plan planOrig = planService.findById(id);
		
		return Response.ok(planOrig)
				.build();
		
	}
}