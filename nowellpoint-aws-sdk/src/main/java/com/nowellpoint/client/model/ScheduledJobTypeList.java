package com.nowellpoint.client.model;

import java.util.Iterator;
import java.util.List;

public class ScheduledJobTypeList implements CollectionResource<ScheduledJobType> {
	
	private List<ScheduledJobType> items;
	
	private int size;
	
	private Meta meta;

	@Override
	public Iterator<ScheduledJobType> iterator() {
		return items.iterator();
	}
	
	public void setSize(int size) {
		this.size = size;
	}

	@Override
	public int getSize() {
		return size;
	}
	
	public void setItems(List<ScheduledJobType> items) {
		this.items = items;
	}

	@Override
	public List<ScheduledJobType> getItems() {
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