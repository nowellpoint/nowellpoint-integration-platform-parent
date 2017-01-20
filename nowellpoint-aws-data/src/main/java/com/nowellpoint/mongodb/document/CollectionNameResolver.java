package com.nowellpoint.mongodb.document;

import com.nowellpoint.mongodb.annotation.Document;

public class CollectionNameResolver {

	public <T> String resolveCollectionName(Class<T> type) {
		return type.getAnnotation(Document.class).collectionName();
	}
}