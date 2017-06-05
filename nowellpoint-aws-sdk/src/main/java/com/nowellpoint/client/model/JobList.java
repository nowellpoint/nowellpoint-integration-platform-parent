package com.nowellpoint.client.model;

import java.util.Iterator;
import java.util.List;

public class JobList implements CollectionResource<Job> {
	
	private List<Job> items;
	
	private int size;
	
	private Meta meta;

	@Override
	public Iterator<Job> iterator() {
		return items.iterator();
	}
	
	public void setSize(int size) {
		this.size = size;
	}

	@Override
	public int getSize() {
		return size;
	}
	
	public void setItems(List<Job> items) {
		this.items = items;
	}

	@Override
	public List<Job> getItems() {
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