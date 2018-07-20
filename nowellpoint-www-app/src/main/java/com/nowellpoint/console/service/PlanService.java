package com.nowellpoint.console.service;

import java.util.List;

import com.nowellpoint.console.model.Plan;

public interface PlanService {
		
	public Plan get(String id);

	public List<Plan> getPlans(String language);
}