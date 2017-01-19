package com.nowellpoint.client.model;

import java.util.Iterator;
import java.util.List;

public class PlanList implements CollectionResource<Plan> {
	
	private List<Plan> items;
	
	private int size;
	
	private Meta meta;

	@Override
	public Iterator<Plan> iterator() {
		return items.iterator();
	}
	
	public void setSize(int size) {
		this.size = size;
	}

	@Override
	public int getSize() {
		return size;
	}
	
	public void setItems(List<Plan> items) {
		this.items = items;
	}

	@Override
	public List<Plan> getItems() {
		return items;
	}
	
	public void setMeta(Meta meta) {
		this.meta = meta;
	}

	@Override
	public Meta getMeta() {
		return meta;
	}
}