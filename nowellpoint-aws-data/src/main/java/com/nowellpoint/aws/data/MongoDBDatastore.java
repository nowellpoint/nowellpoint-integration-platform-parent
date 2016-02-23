package com.nowellpoint.aws.data;

import static com.mongodb.MongoClient.getDefaultCodecRegistry;
import static com.mongodb.MongoClientOptions.builder;
import static org.bson.codecs.configuration.CodecRegistries.fromCodecs;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.types.ObjectId;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.MongoCommandException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.nowellpoint.aws.data.annotation.Handler;
import com.nowellpoint.aws.data.mongodb.AbstractDocument;
import com.nowellpoint.aws.model.admin.Properties;

@WebListener
public class MongoDBDatastore implements ServletContextListener {
	
	private static MongoClientURI mongoClientURI;
	private static MongoClient mongoClient;
	private static MongoDatabase mongoDatabase;
	
	static {
		
		CodecRegistry codecRegistry = fromRegistries(getDefaultCodecRegistry(), fromCodecs(
				new IsoCountryCodec(), 
				new ProjectCodec(), 
				new ApplicationCodec(),
				new IdentityCodec()));
		
		mongoClientURI = new MongoClientURI("mongodb://".concat(System.getProperty(Properties.MONGO_CLIENT_URI)), builder().codecRegistry(codecRegistry));
		mongoClient = new MongoClient(mongoClientURI);		
	}
	
	private MongoDBDatastore() {
		
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
	
	public static void replaceOne(AbstractDocument document) throws JsonProcessingException {	
		getCollection( document ).replaceOne( Filters.eq ( "_id", document.getId() ), document );
	}
	
	public static void insertOne(AbstractDocument document) throws JsonProcessingException {
		getCollection( document ).insertOne( document );
	}
	
	public static void deleteOne(AbstractDocument document) {
		getCollection( document ).deleteOne(  Filters.eq ( "_id", document.getId() ) );
	}
	
	public static void updateOne(String collectionName, ObjectId id, String json) {
		getDatabase().getCollection( collectionName ).updateOne( Filters.eq ( "_id", id ), Document.parse( json ) );
	}
	
	public static void checkStatus() {
		try {
			mongoDatabase.runCommand(new Document("serverStatus", "1"));
		} catch (MongoCommandException ignore) {
			
		}
	}
	
	@SuppressWarnings("unchecked")
	private static <T> MongoCollection<T> getCollection(AbstractDocument document) {
		return (MongoCollection<T>) mongoDatabase.getCollection(document.getClass().getAnnotation(Handler.class).collectionName(), document.getClass());
	}
}