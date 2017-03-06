package com.nowellpoint.mongodb.document;

import java.util.List;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;

import org.bson.Document;
import org.bson.codecs.Codec;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.conversions.Bson;
import org.jboss.logging.Logger;

import com.mongodb.ConnectionString;
import com.mongodb.ReadPreference;
import com.mongodb.WriteConcern;
import com.mongodb.async.client.MongoClient;
import com.mongodb.async.client.MongoClientSettings;
import com.mongodb.async.client.MongoClients;
import com.mongodb.async.client.MongoCollection;
import com.mongodb.async.client.MongoDatabase;
import com.mongodb.connection.ClusterSettings;
import com.mongodb.connection.ConnectionPoolSettings;
import com.mongodb.connection.SocketSettings;
import com.mongodb.connection.SslSettings;
import com.mongodb.connection.netty.NettyStreamFactoryFactory;
import com.nowellpoint.mongodb.DocumentManager;
import com.nowellpoint.mongodb.DocumentManagerFactory;
import com.nowellpoint.util.Assert;

public class DocumentManagerFactoryImpl implements DocumentManagerFactory, AutoCloseable {
	
	private static final Logger LOGGER = Logger.getLogger(DocumentManagerFactory.class);
	
	private static CollectionNameResolver collectionNameResolver = new CollectionNameResolver();
	private static EventLoopGroup eventLoopGroup = new NioEventLoopGroup(); 
	
	private static MongoClient client;
	private static MongoDatabase database;
	
	public DocumentManagerFactoryImpl(ConnectionString connectionString) {
		
		MongoClientSettings settings = MongoClientSettings.builder()
	            .readPreference(ReadPreference.nearest())
	            .codecRegistry(MongoClients.getDefaultCodecRegistry())
	            .connectionPoolSettings(ConnectionPoolSettings.builder().applyConnectionString(connectionString).build())
	            .sslSettings(SslSettings.builder().applyConnectionString(connectionString).build())
	            .streamFactoryFactory(NettyStreamFactoryFactory.builder().eventLoopGroup(eventLoopGroup).build())
	            .writeConcern(WriteConcern.ACKNOWLEDGED)
	            .clusterSettings(ClusterSettings.builder().applyConnectionString(connectionString).build())
	            .credentialList(connectionString.getCredentialList())
	            .socketSettings(SocketSettings.builder().applyConnectionString(connectionString).build())
	            .build();
		
		client = MongoClients.create(settings);
		database = client.getDatabase(connectionString.getDatabase());
		
		LOGGER.info("***Connected to: " + database.getName());
	}
	
	public DocumentManagerFactoryImpl(ConnectionString connectionString, List<Codec<?>> codecs) {
		
		CodecRegistry codecRegistry = CodecRegistries.fromRegistries(
				MongoClients.getDefaultCodecRegistry(), 
				CodecRegistries.fromCodecs(codecs));
		
		MongoClientSettings settings = MongoClientSettings.builder()
	            .readPreference(ReadPreference.nearest())
	            .codecRegistry(codecRegistry)
	            .connectionPoolSettings(ConnectionPoolSettings.builder().applyConnectionString(connectionString).build())
	            .sslSettings(SslSettings.builder().applyConnectionString(connectionString).build())
	            .writeConcern(WriteConcern.ACKNOWLEDGED)
	            .clusterSettings(ClusterSettings.builder().applyConnectionString(connectionString).build())
	            .credentialList(connectionString.getCredentialList())
	            .socketSettings(SocketSettings.builder().applyConnectionString(connectionString).build())
	            .build();
		
		client = MongoClients.create(settings);
		database = client.getDatabase(connectionString.getDatabase());
		
		LOGGER.info("***Connected to: " + database.getName());
	}
	
	@Override
	public DocumentManager createDocumentManager() {
		return new DocumentManagerImpl(this);
	}
	
	@Override
	public MongoCollection<Document> getCollection(String collectionName) {
		Assert.assertNotNullOrEmpty(collectionName, "getCollection function is missing collectionName parameter");
		return (MongoCollection<Document>) database.getCollection(collectionName);
	}
	
	@Override
	public MongoCollection<Document> getCollection(Class<?> documentClass) {
		return (MongoCollection<Document>) getCollection(resolveCollectionName(documentClass));
	}
	
	@Override
	public <T> String resolveCollectionName(Class<T> documentClass) {
		Assert.assertNotNull(documentClass, "resolveCollectionName function is missing documentClass parameter");
		return collectionNameResolver.resolveCollectionName(documentClass);
	}
	
	@Override
	public void close() {
		eventLoopGroup.shutdownGracefully();
		client.close();
		LOGGER.info("***Closed connection to: " + database.getName());
	}
	
	@Override
	public String bsonToString(Bson bson) {
		return bson.toBsonDocument(Document.class, client.getSettings().getCodecRegistry()).toString();
	}

	@Override
	public MongoDatabase getDatabase() {
		return database;
	}
}