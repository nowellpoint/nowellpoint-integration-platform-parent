package com.nowellpoint.api.service;

import com.nowellpoint.api.model.domain.Plan;
import com.nowellpoint.api.model.domain.PlanList;

public interface PlanService {
	
	PlanList getAllActive(String localeSidKey, String languageLocaleKey);
	
	Plan findById(String id);
	
	Plan findByPlanCode(String planCode);
}