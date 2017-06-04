package com.nowellpoint.api.service;

import com.nowellpoint.api.rest.domain.Plan;
import com.nowellpoint.api.rest.domain.PlanList;

public interface PlanService {
	
	public PlanList getAllActive(String locale, String language);
	
	public Plan findById(String id);
	
	public Plan findByPlanCode(String planCode);
}