package com.nowellpoint.api.model.domain;

import java.util.Set;

public class PlanList extends AbstractCollectionResource<Plan, com.nowellpoint.api.model.document.Plan> {
	
	public PlanList(Set<com.nowellpoint.api.model.document.Plan> documents) {
		super(documents);
	}

	@Override
	protected Class<Plan> getItemType() {
		return Plan.class;
	}
}