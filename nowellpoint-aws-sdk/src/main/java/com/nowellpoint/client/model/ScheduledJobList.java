package com.nowellpoint.client.model;

import java.util.Iterator;
import java.util.List;

public class ScheduledJobList implements CollectionResource<ScheduledJob> {
	
	private List<ScheduledJob> items;
	
	private int size;
	
	private Meta meta;

	@Override
	public Iterator<ScheduledJob> iterator() {
		return items.iterator();
	}
	
	public void setSize(int size) {
		this.size = size;
	}

	@Override
	public int getSize() {
		return size;
	}
	
	public void setItems(List<ScheduledJob> items) {
		this.items = items;
	}

	@Override
	public List<ScheduledJob> getItems() {
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