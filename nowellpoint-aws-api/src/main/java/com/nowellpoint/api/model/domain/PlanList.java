package com.nowellpoint.api.model.domain;

import com.mongodb.client.FindIterable;

public class PlanList extends AbstractCollectionResource<Plan, com.nowellpoint.api.model.document.Plan> {
	
	public PlanList(FindIterable<com.nowellpoint.api.model.document.Plan> documents) {
		super(documents);
	}

	@Override
	protected Class<Plan> getItemType() {
		return Plan.class;
	}
}