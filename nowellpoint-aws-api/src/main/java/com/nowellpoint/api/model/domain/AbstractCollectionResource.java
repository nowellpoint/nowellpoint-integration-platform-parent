package com.nowellpoint.api.model.domain;

import java.lang.reflect.Constructor;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.mongodb.Block;
import com.mongodb.client.FindIterable;
import com.nowellpoint.mongodb.document.MongoDocument;

public abstract class AbstractCollectionResource<R extends AbstractResource, D extends MongoDocument> implements CollectionResource<R> {
	
	private Set<R> items = new HashSet<R>();
	private Meta meta = new Meta();
	
	public AbstractCollectionResource(FindIterable<D> documents) {
		documents.forEach(new Block<D>() {
			@Override
			public void apply(final D document) {
				try {
					@SuppressWarnings("unchecked")
					Constructor<R> constructor = (Constructor<R>) Class.forName(getItemType().getName()).getConstructor(MongoDocument.class);
					items.add(constructor.newInstance(document));
				} catch (Exception e) {
					e.printStackTrace();
				} 
		    }
		});
	}
	
	public AbstractCollectionResource(Set<D> documents) {
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