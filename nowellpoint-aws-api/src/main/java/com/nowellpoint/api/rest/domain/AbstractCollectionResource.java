package com.nowellpoint.api.rest.domain;

import java.lang.reflect.Constructor;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.nowellpoint.mongodb.document.MongoDocument;

public abstract class AbstractCollectionResource<R extends AbstractResource, D extends MongoDocument> implements CollectionResource<R> {
	
	private Set<R> items = new HashSet<R>();
	private Meta meta = new Meta();
	
	public AbstractCollectionResource(Set<D> documents) {
		if (documents != null && ! documents.isEmpty()) {
			documents.forEach(document -> {
				try {
					@SuppressWarnings("unchecked")
					Constructor<R> constructor = (Constructor<R>) Class.forName(getItemType().getName()).getConstructor(MongoDocument.class);
					items.add(constructor.newInstance(document));
				} catch (Exception e) {
					e.printStackTrace();
				}
			});
		}
	}
	
	protected abstract Class<R> getItemType();
	
	@Override
	public Meta getMeta() {
		return meta;
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