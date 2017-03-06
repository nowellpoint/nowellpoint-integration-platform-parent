package com.nowellpoint.mongodb.document;

import com.nowellpoint.mongodb.annotation.Document;
import com.nowellpoint.util.Assert;

public class CollectionNameResolver {

	public <T> String resolveCollectionName(Class<T> type) {
		String collectionName = type.isAnnotationPresent(Document.class) ? type.getAnnotation(Document.class).collectionName() : null;
		Assert.assertNotNull(collectionName, String.format("Unable to resolve collection name for %s. Add @Document annotation to class to resolve", type.getName()));
		return collectionName;
	}
}