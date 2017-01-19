package com.nowellpoint.mongodb.document;

import static com.mongodb.MongoClient.getDefaultCodecRegistry;
import static com.mongodb.MongoClientOptions.builder;
import static org.bson.codecs.configuration.CodecRegistries.fromCodecs;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

import java.util.List;
import java.util.logging.Logger;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.bson.Document;
import org.bson.codecs.Codec;
import org.bson.codecs.configuration.CodecRegistry;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.MongoCommandException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.nowellpoint.util.Properties;

@WebListener
public class MongoDatastore implements ServletContextListener {
	
	private static final Logger LOGGER = Logger.getLogger(MongoDatastore.class.getName());
	
	private static MongoClientURI mongoClientURI;
	private static MongoClient mongoClient;
	private static MongoDatabase mongoDatabase;
	private static CollectionNameResolver collectionNameResolver = new CollectionNameResolver();
	
	private MongoDatastore() {
		
	}
	
	public static void registerCodecs(List<Codec<?>> codecs) {
		
		CodecRegistry codecRegistry = fromRegistries(getDefaultCodecRegistry(), fromCodecs(codecs));
		
		mongoClientURI = new MongoClientURI("mongodb://".concat(System.getProperty(Properties.MONGO_CLIENT_URI)), builder().codecRegistry(codecRegistry));
	}
	
	@Override
	public void contextInitialized(ServletContextEvent event) {
		mongoClient = new MongoClient(mongoClientURI);	
		mongoDatabase = mongoClient.getDatabase(mongoClientURI.getDatabase());
		LOGGER.info(mongoDatabase.getName());
	}
	
	@Override
	public void contextDestroyed(ServletContextEvent event) {
		mongoClient.close();
	}
	
	public static MongoDatabase getDatabase() {			
		return mongoDatabase;
	}
	
	public static Document checkStatus() {
		try {
			return mongoDatabase.runCommand(new Document("serverStatus", "1"));
		} catch (MongoCommandException ignore) {
			return null;
		}
	}
	
	public static <T> String getCollectionName(Class<T> type) {
		return collectionNameResolver.resolveDocument(type);
	}
	
	@SuppressWarnings("unchecked")
	public static <T> MongoCollection<T> getCollection(MongoDocument document) {
		return (MongoCollection<T>) mongoDatabase.getCollection(getCollectionName(document.getClass()), document.getClass());
	}
	
	public static <T> MongoCollection<T> getCollection(Class<T> type) {
		return (MongoCollection<T>) mongoDatabase.getCollection(getCollectionName(type), type);
	}
}