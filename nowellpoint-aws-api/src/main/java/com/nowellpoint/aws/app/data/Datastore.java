package com.nowellpoint.aws.app.data;

import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoClientURI;
import com.mongodb.MongoCommandException;
import com.mongodb.client.MongoDatabase;

public class Datastore {
	
	private static MongoClientURI mongoClientURI;
	private static MongoClient mongoClient;
	private static MongoDatabase mongoDatabase;
	
	static {
		
		CodecRegistry codecRegistry = CodecRegistries.fromRegistries(
			    MongoClient.getDefaultCodecRegistry(),
			    CodecRegistries.fromCodecs(new IsoCountryCodec())
		);
		
		mongoClientURI = new MongoClientURI("mongodb://".concat(System.getenv("MONGO_CLIENT_URI")), MongoClientOptions.builder().codecRegistry(codecRegistry));
		mongoClient = new MongoClient(mongoClientURI);		
	}
	
	private Datastore() {
		
	}
	
	public static void connect() {		
		mongoDatabase = mongoClient.getDatabase(mongoClientURI.getDatabase());
	}
	
	public static void checkStatus() {
		try {
			mongoDatabase.runCommand(new Document("serverStatus", "1"));
		} catch (MongoCommandException ignore) {
			
		}
	}

	public static MongoDatabase getDatabase() {			
		return mongoDatabase;
	}
	
	public static void close() {
		mongoClient.close();
	}
}