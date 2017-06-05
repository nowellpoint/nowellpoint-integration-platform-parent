package com.nowellpoint.client.model;

import java.util.Iterator;
import java.util.List;

public class IsoCountryList implements CollectionResource<IsoCountry> {
	
	private List<IsoCountry> items;
	
	private int size;
	
	private Meta meta;

	@Override
	public Iterator<IsoCountry> iterator() {
		return items.iterator();
	}
	
	public void setSize(int size) {
		this.size = size;
	}

	@Override
	public int getSize() {
		return size;
	}
	
	public void setItems(List<IsoCountry> items) {
		this.items = items;
	}

	@Override
	public List<IsoCountry> getItems() {
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