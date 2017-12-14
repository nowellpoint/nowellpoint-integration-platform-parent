package com.nowellpoint.client.model;

import java.util.Iterator;
import java.util.List;

public class ConnectorList implements CollectionResource<Connector> {
	
	private List<Connector> items;
	
	private int size;
	
	private Meta meta;

	@Override
	public Iterator<Connector> iterator() {
		return items.iterator();
	}

	@Override
	public int getSize() {
		return size;
	}

	@Override
	public List<Connector> getItems() {
		return items;
	}

	@Override
	public Meta getMeta() {
		return meta;
	}
}