package com.nowellpoint.aws.api.data;

import static com.mongodb.MongoClient.getDefaultCodecRegistry;
import static com.mongodb.MongoClientOptions.builder;
import static org.bson.codecs.configuration.CodecRegistries.fromCodecs;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistry;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.MongoCommandException;
import com.mongodb.client.MongoDatabase;

public class Datastore {
	
	private static MongoClientURI mongoClientURI;
	private static MongoClient mongoClient;
	private static MongoDatabase mongoDatabase;
	
	static {
		
		CodecRegistry codecRegistry = fromRegistries(getDefaultCodecRegistry(), fromCodecs(new IsoCountryCodec()));
		
		mongoClientURI = new MongoClientURI("mongodb://".concat(System.getenv("MONGO_CLIENT_URI")), builder().codecRegistry(codecRegistry));
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