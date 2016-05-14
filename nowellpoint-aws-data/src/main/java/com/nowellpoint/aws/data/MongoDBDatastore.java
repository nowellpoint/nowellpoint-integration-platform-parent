package com.nowellpoint.aws.data;

import static com.mongodb.MongoClient.getDefaultCodecRegistry;
import static com.mongodb.MongoClientOptions.builder;
import static org.bson.codecs.configuration.CodecRegistries.fromCodecs;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

import java.util.List;
import java.util.concurrent.Executors;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.bson.Document;
import org.bson.codecs.Codec;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.types.ObjectId;
import org.joda.time.Instant;

import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sns.model.PublishRequest;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.MongoCommandException;
import com.mongodb.MongoException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.nowellpoint.aws.data.annotation.Audited;
import com.nowellpoint.aws.data.mongodb.AbstractDocument;
import com.nowellpoint.aws.data.mongodb.AuditHistory;
import com.nowellpoint.aws.model.admin.Properties;

@WebListener
public class MongoDBDatastore implements ServletContextListener {
	
	private static MongoClientURI mongoClientURI;
	private static MongoClient mongoClient;
	private static MongoDatabase mongoDatabase;
	
	private MongoDBDatastore() {
		
	}
	
	public static void registerCodecs(List<Codec<?>> codecs) {
		
		codecs.add(new AuditHistoryCodec());
		
		CodecRegistry codecRegistry = fromRegistries(getDefaultCodecRegistry(), fromCodecs(codecs));
		
		mongoClientURI = new MongoClientURI("mongodb://".concat(System.getProperty(Properties.MONGO_CLIENT_URI)), builder().codecRegistry(codecRegistry));
	}
	
	@Override
	public void contextInitialized(ServletContextEvent event) {
		mongoClient = new MongoClient(mongoClientURI);	
		mongoDatabase = mongoClient.getDatabase(mongoClientURI.getDatabase());
	}
	
	@Override
	public void contextDestroyed(ServletContextEvent event) {
		mongoClient.close();
	}
	
	public static MongoDatabase getDatabase() {			
		return mongoDatabase;
	}
	
	public static void replaceOne(AbstractDocument document) {	
		document.setLastModifiedDate(Instant.now().toDate());
		Executors.newSingleThreadExecutor().execute(new Runnable() {
			@Override
			public void run() {
				try {
					getCollection( document ).replaceOne( Filters.eq ( "_id", document.getId() ), document );
					if ( document.getClass().isAnnotationPresent( Audited.class ) ) {
						audit( document, AuditHistory.Event.UPDATE );
					}
				} catch (MongoException e) {
					publish(e);
					e.printStackTrace();
				}
			}
		});
	}
	
	public static void insertOne(AbstractDocument document) {
		document.setId(new ObjectId());
		document.setCreatedDate(Instant.now().toDate());
		document.setLastModifiedDate(Instant.now().toDate());
		Executors.newSingleThreadExecutor().execute(new Runnable() {
			@Override
			public void run() {
				try {
					getCollection( document ).insertOne( document );
					if ( document.getClass().isAnnotationPresent( Audited.class ) ) {
						audit( document, AuditHistory.Event.INSERT );
					}
				} catch (MongoException e) {
					publish(e);
					e.printStackTrace();
				}
			}
		});
	}
	
	public static void deleteOne(AbstractDocument document) {
		Executors.newSingleThreadExecutor().execute(new Runnable() {
			@Override
			public void run() {
				try {
					getCollection( document ).deleteOne(  Filters.eq ( "_id", document.getId() ) );
					if ( document.getClass().isAnnotationPresent( Audited.class ) ) {
						audit( document, AuditHistory.Event.DELETE );
					}
				} catch (MongoException e) {
					publish(e);
					e.printStackTrace();
				}
			}
		});
	}
	
	public static <T extends AbstractDocument> void updateOne(Class<T> documentClass, ObjectId id, String json) {
		getCollection( documentClass ).updateOne( Filters.eq ( "_id", id ), Document.parse( json ) );
	}
	
	public static void checkStatus() {
		try {
			mongoDatabase.runCommand(new Document("serverStatus", "1"));
		} catch (MongoCommandException ignore) {
			
		}
	}
	
	public static <T extends AbstractDocument> String getCollectionName(Class<T> type) {
		return type.getAnnotation(com.nowellpoint.aws.data.annotation.Document.class).collectionName();
	}
	
	@SuppressWarnings("unchecked")
	public static <T extends AbstractDocument> MongoCollection<T> getCollection(AbstractDocument document) {
		return (MongoCollection<T>) mongoDatabase.getCollection(getCollectionName(document.getClass()), document.getClass());
	}
	
	public static <T extends AbstractDocument> MongoCollection<T> getCollection(Class<T> type) {
		return (MongoCollection<T>) mongoDatabase.getCollection(getCollectionName(type), type);
	}
	
	private static void audit( AbstractDocument document, AuditHistory.Event event ) {
		String collectionName = getCollectionName( document.getClass() ).concat( ".history" );
		
		MongoCollection<AuditHistory> collection = mongoDatabase.getCollection( collectionName, AuditHistory.class );
				
		AuditHistory auditHistory = new AuditHistory()
				.withSourceId(document.getId())
				.withCreatedById(document.getLastModifiedById())
				.withDocument(document)
				.withEvent(event)
				.withLastModifiedById(document.getLastModifiedById());
				
		collection.insertOne( auditHistory );
	}
	
	private static void publish(MongoException exception) {
		AmazonSNS snsClient = new AmazonSNSClient();
		PublishRequest publishRequest = new PublishRequest("arn:aws:sns:us-east-1:600862814314:MONGODB_EXCEPTION", exception.getMessage());
		snsClient.publish(publishRequest);
	}
}