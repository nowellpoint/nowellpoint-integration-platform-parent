package com.nowellpoint.api.rest.domain;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.jboss.logging.Logger;

import com.nowellpoint.mongodb.document.MongoDocument;

public abstract class AbstractImmutableCollectionResource<R extends AbstractImmutableResource, D extends MongoDocument> implements CollectionResource<R> {
	
	private static final Logger LOGGER = Logger.getLogger(AbstractImmutableCollectionResource.class);
	
	private Set<R> items = new HashSet<R>();
	
	public AbstractImmutableCollectionResource(Set<D> documents) {
		if (documents != null && ! documents.isEmpty()) {
			documents.forEach(document -> {
				try {
					String className = getItemType().getSuperclass().getName();
					System.out.println(className);
					Method method = Class.forName(className).getDeclaredMethod("of", MongoDocument.class);
					@SuppressWarnings("unchecked")
					R object = (R) method.invoke(null, document);
					items.add(object);
				} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | ClassNotFoundException e) {
					LOGGER.info("Unable to invoke of method for: " + getItemType().getSuperclass().getName());
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