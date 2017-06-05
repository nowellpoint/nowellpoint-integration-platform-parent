package com.nowellpoint.client.model;

import java.util.Iterator;
import java.util.List;

public class SalesforceConnectorList implements CollectionResource<SalesforceConnector> {
	
	private List<SalesforceConnector> items;
	
	private int size;
	
	private Meta meta;

	@Override
	public Iterator<SalesforceConnector> iterator() {
		return items.iterator();
	}
	
	public void setSize(int size) {
		this.size = size;
	}

	@Override
	public int getSize() {
		return size;
	}
	
	public void setItems(List<SalesforceConnector> items) {
		this.items = items;
	}

	@Override
	public List<SalesforceConnector> getItems() {
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