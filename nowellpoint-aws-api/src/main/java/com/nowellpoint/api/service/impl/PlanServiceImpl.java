package com.nowellpoint.api.service.impl;

import com.nowellpoint.api.model.domain.Plan;
import com.nowellpoint.api.model.domain.PlanList;
import com.nowellpoint.api.service.PlanService;

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