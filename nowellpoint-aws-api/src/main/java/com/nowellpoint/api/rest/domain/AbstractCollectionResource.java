package com.nowellpoint.api.rest.domain;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.jboss.logging.Logger;

import com.nowellpoint.mongodb.document.MongoDocument;

public abstract class AbstractCollectionResource<R extends AbstractResource, D extends MongoDocument> implements CollectionResource<R> {
	
	private static final Logger LOGGER = Logger.getLogger(AbstractCollectionResource.class);
	
	private Set<R> items = new HashSet<R>();
	
	public AbstractCollectionResource(Set<D> documents) {
		if (documents != null && ! documents.isEmpty()) {
			documents.forEach(document -> {
				try {
					Method method = Class.forName(getItemType().getName()).getMethod("of", MongoDocument.class);
					@SuppressWarnings("unchecked")
					R object = (R) method.invoke(null, document);
					items.add(object);
				} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | ClassNotFoundException e) {
					LOGGER.info("Unable to invoke of method for: " + getItemType().getName());
					e.printStackTrace();
				}
			});
		}
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