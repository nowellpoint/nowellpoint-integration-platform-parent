package com.nowellpoint.aws.data.test;

import java.util.concurrent.CountDownLatch;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.junit.Test;

import com.mongodb.Block;
import com.mongodb.ConnectionString;
import com.mongodb.async.SingleResultCallback;
import com.mongodb.async.client.FindIterable;
import com.mongodb.async.client.MongoClient;
import com.mongodb.async.client.MongoClients;
import com.mongodb.async.client.MongoCollection;
import com.mongodb.async.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.nowellpoint.util.Properties;

public class TestMongoClientAsync {
	
	@Test
	public void testMongoClientConnect() {
		Properties.loadProperties(System.getenv("NOWELLPOINT_PROPERTY_STORE"));
		
		final CountDownLatch latch = new CountDownLatch(1);
		
		SingleResultCallback<Void> callback = new SingleResultCallback<Void>() {
		    @Override
		    public void onResult(final Void result, final Throwable t) {
		    	if (t != null) {
		    		System.out.println(t.getMessage());
		    	}
		        latch.countDown();
		    }
		};
		
		ConnectionString connectionString = new ConnectionString("mongodb://".concat(Properties.getProperty(Properties.MONGO_CLIENT_URI)));
		
		MongoClient mongoClient = MongoClients.create(connectionString);
		MongoDatabase mongoDatabase = mongoClient.getDatabase(connectionString.getDatabase());
		MongoCollection<Document> collection = mongoDatabase.getCollection("account.profiles");
		FindIterable<Document> documents = collection.withDocumentClass(Document.class).find( Filters.eq ( "_id", new ObjectId( "5808408e392e00330aeef78d" ) ) );
		documents.forEach(new Block<Document>() {
			@Override
		       public void apply(final Document document) {
		           System.out.println(document.toJson());
		       }
		}, callback);
		
		try {
			latch.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			mongoClient.close();
		}
	}	
}