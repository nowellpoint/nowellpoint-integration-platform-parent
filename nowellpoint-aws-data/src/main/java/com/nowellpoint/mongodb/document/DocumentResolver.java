package com.nowellpoint.mongodb.document;

import com.nowellpoint.mongodb.annotation.Document;

public class DocumentResolver {

	public <T extends MongoDocument> String resolveDocument(Class<T> type) {
		return type.getAnnotation(Document.class).collectionName();
	}
}