package com.nowellpoint.aws.tools;

import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;

public class MongoQuery {
	
	private MongoDatabase mongoDatabase;
	private String collectionName;
	private Bson filter;
	
	public MongoQuery() {
		
	}
	
	public MongoQuery withCollectionName(String collectionName) {
		this.collectionName = collectionName;
		return this;
	}
	
	public MongoQuery withMongoDatabase(MongoDatabase mongoDatabase) {
		this.mongoDatabase = mongoDatabase;
		return this;
	}
	
	public MongoQuery withSalesforceId(String salesforceId) {
		filter = Filters.eq ( "salesforceId", salesforceId );
		return this;
	}
	
	public MongoQuery withFilter(Bson filter) {
		this.filter = filter;
		return this;
	}

	public Document find() {
		MongoCollection<Document> mongoCollection = mongoDatabase.getCollection(collectionName);
		Document document = mongoCollection.find( filter ).first();
		return document;
	}
}