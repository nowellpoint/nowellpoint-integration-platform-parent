package com.nowellpoint.client.model;

import java.util.Iterator;
import java.util.List;

public class JobScheduleList implements CollectionResource<JobSchedule> {
	
	private List<JobSchedule> items;
	
	private int size;
	
	private Meta meta;

	@Override
	public Iterator<JobSchedule> iterator() {
		return items.iterator();
	}
	
	public void setSize(int size) {
		this.size = size;
	}

	@Override
	public int getSize() {
		return size;
	}
	
	public void setItems(List<JobSchedule> items) {
		this.items = items;
	}

	@Override
	public List<JobSchedule> getItems() {
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