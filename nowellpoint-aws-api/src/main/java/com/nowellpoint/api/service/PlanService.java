package com.nowellpoint.api.service;

import java.util.Set;

import com.nowellpoint.api.model.domain.Plan;
import com.nowellpoint.api.model.mapper.PlanModelMapper;
import com.nowellpoint.util.Assert;

public class PlanService extends PlanModelMapper {

	public PlanService() {
		super();
	}
	
	public Set<Plan> getAllActive(String localeSidKey, String languageLocaleKey) {
		if (Assert.isNull(localeSidKey)) {
			throw new IllegalArgumentException("Missing localeSidKey query parameter");
		}
		
		if (Assert.isNull(languageLocaleKey)) {
			throw new IllegalArgumentException("Missing languageLocaleKey query parameter");
		}
		
		return super.getAllActive(localeSidKey, languageLocaleKey);
	}
	
	public Plan findPlan(String id) {
		return super.findPlan(id);
	}
	
	public Plan findByPlanCode(String planCode) {
		return super.findByPlanCode(planCode);
	}
}