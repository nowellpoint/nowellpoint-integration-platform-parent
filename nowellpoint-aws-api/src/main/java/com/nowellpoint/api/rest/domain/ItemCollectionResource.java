package com.nowellpoint.api.rest.domain;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public abstract class ItemCollectionResource<R extends Resource> implements CollectionResource<R> {
	
	private Set<R> items = new HashSet<R>();
	
	public ItemCollectionResource(Set<R> items) {
		this.items = items;
	}
	
	protected abstract Class<R> getItemType();
	
	@Override
	public Meta getMeta() {
		return null;
	}

	@Override
	public Iterator<R> iterator() {
		return items.iterator();
	}

	@Override
	public int getSize() {
		return items.size();
	}

	@Override
	public Set<R> getItems() {
		return items;
	}
}