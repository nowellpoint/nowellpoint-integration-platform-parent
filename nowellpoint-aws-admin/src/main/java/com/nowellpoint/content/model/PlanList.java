package com.nowellpoint.content.model;

import java.util.Collections;
import java.util.List;

import com.amazonaws.services.s3.model.S3Object;

public class PlanList extends S3Entity<Plan> {
	
	private List<Plan> items = Collections.emptyList();
	
	public PlanList(S3Object object) {
		items = getCollection(Plan.class, object);
	}

	public List<Plan> getItems() {
		return items;
	}
	
	public Integer getSize() {
		return items.size();
	}
}