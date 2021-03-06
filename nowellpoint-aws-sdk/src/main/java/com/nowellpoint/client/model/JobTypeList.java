package com.nowellpoint.client.model;

import java.util.Iterator;
import java.util.List;

public class JobTypeList implements CollectionResource<JobType> {
	
	private List<JobType> items;
	
	private int size;
	
	private Meta meta;

	@Override
	public Iterator<JobType> iterator() {
		return items.iterator();
	}
	
	public void setSize(int size) {
		this.size = size;
	}

	@Override
	public int getSize() {
		return size;
	}
	
	public void setItems(List<JobType> items) {
		this.items = items;
	}

	@Override
	public List<JobType> getItems() {
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