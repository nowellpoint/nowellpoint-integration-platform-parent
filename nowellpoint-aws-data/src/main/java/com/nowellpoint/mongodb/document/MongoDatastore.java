package com.nowellpoint.mongodb.document;

import static com.mongodb.MongoClient.getDefaultCodecRegistry;
import static com.mongodb.MongoClientOptions.builder;
import static com.mongodb.client.model.Filters.eq;
import static org.bson.codecs.configuration.CodecRegistries.fromCodecs;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.bson.Document;
import org.bson.codecs.Codec;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sns.model.PublishRequest;
import com.mongodb.Block;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.MongoCommandException;
import com.mongodb.MongoException;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.nowellpoint.util.Properties;

@WebListener
public class MongoDatastore implements ServletContextListener {
	
	private static final Logger LOGGER = Logger.getLogger(MongoDatastore.class.getName());
	
	private static MongoClientURI mongoClientURI;
	private static MongoClient mongoClient;
	private static MongoDatabase mongoDatabase;
	private static DocumentResolver documentResolver = new DocumentResolver();
	
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
	
	public static void replaceOne(MongoDocument document) {	
		Executors.newSingleThreadExecutor().execute(new Runnable() {
			@Override
			public void run() {
				try {
					getCollection( document ).replaceOne( Filters.eq ( "_id", document.getId() ), document );
				} catch (MongoException e) {
					publish(e);
					e.printStackTrace();
				}
			}
		});
	}
	
	public static void replaceOne(ObjectId id, MongoDocument document) {	
		Executors.newSingleThreadExecutor().execute(new Runnable() {
			@Override
			public void run() {
				try {
					getCollection( document ).replaceOne( Filters.eq ( "_id", id ), document );
				} catch (MongoException e) {
					publish(e);
					e.printStackTrace();
				}
			}
		});
	}
	
	public static void replaceOne(Bson filter, MongoDocument document) {	
		Executors.newSingleThreadExecutor().execute(new Runnable() {
			@Override
			public void run() {
				try {
					getCollection( document ).replaceOne( filter, document );
				} catch (MongoException e) {
					publish(e);
					e.printStackTrace();
				}
			}
		});
	}
	
	public static void insertOne(MongoDocument document) {
		Executors.newSingleThreadExecutor().execute(new Runnable() {
			@Override
			public void run() {
				try {
					getCollection( document ).insertOne( document );
				} catch (MongoException e) {
					publish(e);
					e.printStackTrace();
				}
			}
		});
	}
	
	public static void deleteOne(MongoDocument document) {
		Executors.newSingleThreadExecutor().execute(new Runnable() {
			@Override
			public void run() {
				try {
					getCollection( document ).deleteOne(  Filters.eq ( "_id", document.getId() ) );
				} catch (MongoException e) {
					publish(e);
					e.printStackTrace();
				}
			}
		});
	}
	
	public static void deleteMany(String collectionName, Bson query) {
		Executors.newSingleThreadExecutor().execute(new Runnable() {
			@Override
			public void run() {
				try {
					getDatabase().getCollection( collectionName ).deleteMany( query );
				} catch (MongoException e) {
					publish(e);
					e.printStackTrace();
				}
			}
		});
	}
	
	public static <T extends MongoDocument> T findOne(Class<T> documentClass, Bson query) {
		
		String collectionName = getCollectionName( documentClass );
		
		T document = getDatabase()
				.getCollection( collectionName )
				.withDocumentClass( documentClass )
				.find( query )
				.first();
		
		if (document == null) {
			throw new DocumentNotFoundException(String.format( "Document of type: %s was not found: %s", documentClass.getSimpleName(), query.toBsonDocument(Document.class, getDefaultCodecRegistry()).toString()) );
		}
		
		return document;
	}
	
	public static <T extends MongoDocument> Set<T> find(Class<T> documentClass, Bson query) {
		
		String collectionName = getCollectionName( documentClass );
		
		Set<T> documents = new HashSet<T>();
		
		FindIterable<T> search = getDatabase()
				.getCollection( collectionName )
				.withDocumentClass( documentClass )
				.find( query );
		
		search.forEach(new Block<T>() {
			@Override
			public void apply(final T document) {
				documents.add(document);
		    }
		});
		
		return documents;
	}
	
	public static <T extends MongoDocument> T findById(Class<T> documentClass, ObjectId id) {	
		
		String collectionName = getCollectionName( documentClass );
		
		T document = getDatabase()
					.getCollection( collectionName )
					.withDocumentClass( documentClass )
					.find( eq ( "_id", id ) )
					.first();
		
		if (document == null) {
			throw new DocumentNotFoundException(String.format( "Resource of type: %s for Id: %s was not found", documentClass.getSimpleName(), id.toString() ) );
		}
		
		return document;
	}
	
	public static <T extends MongoDocument> void updateOne(Class<T> documentClass, ObjectId id, String json) {
		getCollection( documentClass ).updateOne( Filters.eq ( "_id", id ), Document.parse( json ) );
	}
	
	public static Document checkStatus() {
		try {
			return mongoDatabase.runCommand(new Document("serverStatus", "1"));
		} catch (MongoCommandException ignore) {
			return null;
		}
	}
	
	public static <T extends MongoDocument> String getCollectionName(Class<T> type) {
		return documentResolver.resolveDocument(type);
	}
	
	@SuppressWarnings("unchecked")
	public static <T extends MongoDocument> MongoCollection<T> getCollection(MongoDocument document) {
		return (MongoCollection<T>) mongoDatabase.getCollection(getCollectionName(document.getClass()), document.getClass());
	}
	
	public static <T extends MongoDocument> MongoCollection<T> getCollection(Class<T> type) {
		return (MongoCollection<T>) mongoDatabase.getCollection(getCollectionName(type), type);
	}
	
	private static void publish(MongoException exception) {
		AmazonSNS snsClient = new AmazonSNSClient();
		PublishRequest publishRequest = new PublishRequest("arn:aws:sns:us-east-1:600862814314:MONGODB_EXCEPTION", exception.getMessage());
		snsClient.publish(publishRequest);
	}
}