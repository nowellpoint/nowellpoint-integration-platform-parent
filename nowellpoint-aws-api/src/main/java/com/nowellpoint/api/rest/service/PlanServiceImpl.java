package com.nowellpoint.api.rest.service;

import com.nowellpoint.api.rest.PlanService;
import com.nowellpoint.api.rest.domain.Plan;
import com.nowellpoint.api.rest.domain.PlanList;

public class PlanServiceImpl extends AbstractPlanService implements PlanService {

	public PlanServiceImpl() {
		super();
	}
	
	@Override
	public PlanList getAllActive(String localeSidKey, String languageLocaleKey) {
		return super.getAllActive(localeSidKey, languageLocaleKey);
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