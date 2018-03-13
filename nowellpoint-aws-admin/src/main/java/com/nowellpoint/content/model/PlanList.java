package com.nowellpoint.content.model;

import java.util.List;

public class PlanList {
	
	private List<Plan> items = null;
	
	public PlanList(List<Plan> items) {
		this.items = items;
	}

	public List<Plan> getItems() {
		return items;
	}
	
	public Integer getSize() {
		return items.size();
	}
}