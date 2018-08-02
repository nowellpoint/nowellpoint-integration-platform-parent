package com.nowellpoint.console.service;

import java.util.List;

import com.nowellpoint.console.model.Plan;

public interface PlanService {
		
	public Plan get(String id);
	
	public Plan getByCode(String planCode);

	public List<Plan> getPlans(String language);
}