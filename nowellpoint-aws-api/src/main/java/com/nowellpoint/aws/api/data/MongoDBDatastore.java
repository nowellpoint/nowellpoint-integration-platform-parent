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
import org.bson.types.ObjectId;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.MongoCommandException;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
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
	
	public MongoDBDatastore() {
		
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
	
	public static void updateOne(String collectionName, ObjectId id, Document document) {
		getDatabase().getCollection( collectionName ).updateOne( Filters.eq ( "_id", id ), new Document( "$set", document ) );
	}
	
	public static void insertOne(String collectionName, Document document) {
		getDatabase().getCollection( collectionName ).insertOne( document );
	}
	
	public static void deleteOne(String collectionName, ObjectId id) {
		getDatabase().getCollection( collectionName ).deleteOne(  Filters.eq ( "_id", id ) );
	}
	
	public static void checkStatus() {
		try {
			mongoDatabase.runCommand(new Document("serverStatus", "1"));
		} catch (MongoCommandException ignore) {
			
		}
	}
}