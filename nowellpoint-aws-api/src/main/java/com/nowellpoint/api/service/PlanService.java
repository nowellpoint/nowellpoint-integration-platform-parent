package com.nowellpoint.api.service;

import java.util.Set;

import com.nowellpoint.api.model.dto.Plan;
import com.nowellpoint.api.model.mapper.PlanModelMapper;

public class PlanService extends PlanModelMapper {

	public PlanService() {
		super();
	}
	
	public Set<Plan> getAllActive(String localeSidKey, String languageLocaleKey) {
		return super.getAllActive(localeSidKey, languageLocaleKey);
	}
	
	public Plan findByPlanCode(String planCode) {
		return super.findByPlanCode(planCode);
	}
}