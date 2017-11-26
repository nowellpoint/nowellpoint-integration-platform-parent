package com.nowellpoint.api.service;

import com.nowellpoint.api.rest.domain.PlanOrig;
import com.nowellpoint.api.rest.domain.PlanList;

public interface PlanService {
	
	public PlanList getAllActive(String locale, String language);
	
	public PlanOrig findById(String id);
	
	public PlanOrig findByPlanCode(String planCode);
}