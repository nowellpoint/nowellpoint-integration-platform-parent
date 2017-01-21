package com.nowellpoint.mongodb;

import com.mongodb.async.client.MongoDatabase;

public interface DocumentManagerFactory {
	
	MongoDatabase getDatabase();
	
	void close();

}
