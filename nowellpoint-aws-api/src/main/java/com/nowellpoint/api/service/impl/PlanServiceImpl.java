package com.nowellpoint.api.service.impl;

import com.nowellpoint.api.rest.domain.Plan;
import com.nowellpoint.api.rest.domain.PlanList;
import com.nowellpoint.api.service.PlanService;

public class PlanServiceImpl extends AbstractPlanService implements PlanService {

	public PlanServiceImpl() {
		super();
	}
	
	@Override
	public PlanList getAllActive(String locale, String language) {
		return super.getAllActive(locale, language);
	}
	
	@Override
	public Plan findById(String id) {
		return super.findById(id);
	}
	
	@Override
	public Plan findByPlanCode(String planCode) {
		return super.findByPlanCode(planCode);
	}
}