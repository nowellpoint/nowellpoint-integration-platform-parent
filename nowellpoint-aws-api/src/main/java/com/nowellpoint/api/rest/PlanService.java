package com.nowellpoint.api.rest;

import com.nowellpoint.api.rest.domain.Plan;
import com.nowellpoint.api.rest.domain.PlanList;

public interface PlanService {
	
	PlanList getAllActive(String locale, String language);
	
	Plan findById(String id);
	
	Plan findByPlanCode(String planCode);
}