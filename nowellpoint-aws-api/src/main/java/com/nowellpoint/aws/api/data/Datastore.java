package com.nowellpoint.aws.api.data;

import static com.mongodb.MongoClient.getDefaultCodecRegistry;
import static com.mongodb.MongoClientOptions.builder;
import static org.bson.codecs.configuration.CodecRegistries.fromCodecs;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistry;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.MongoCommandException;
import com.mongodb.client.MongoDatabase;
import com.nowellpoint.aws.provider.ConfigurationProvider;

@WebListener
public class Datastore implements ServletContextListener {
	
	private static MongoClientURI mongoClientURI;
	private static MongoClient mongoClient;
	private static MongoDatabase mongoDatabase;
	
	static {
		
		CodecRegistry codecRegistry = fromRegistries(getDefaultCodecRegistry(), fromCodecs(new IsoCountryCodec()));
		
		mongoClientURI = new MongoClientURI("mongodb://".concat(ConfigurationProvider.getMongoClientUri()), builder().codecRegistry(codecRegistry));
		mongoClient = new MongoClient(mongoClientURI);		
	}
	
	public Datastore() {
		
	}
	
	@Override
	public void contextInitialized(ServletContextEvent event) {
		mongoDatabase = mongoClient.getDatabase(mongoClientURI.getDatabase());
	}
	
	@Override
	public void contextDestroyed(ServletContextEvent event) {
		mongoClient.close();
	}
	
	public static MongoDatabase getDatabase() {			
		return mongoDatabase;
	}
	
	public static void checkStatus() {
		try {
			mongoDatabase.runCommand(new Document("serverStatus", "1"));
		} catch (MongoCommandException ignore) {
			
		}
	}
}