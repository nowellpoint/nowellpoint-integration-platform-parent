package com.nowellpoint.aws.event;

import java.io.IOException;
import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

import org.bson.Document;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.MongoException;
import com.mongodb.client.MongoDatabase;
import com.nowellpoint.aws.CacheManager;
import com.nowellpoint.aws.client.DataClient;
import com.nowellpoint.aws.model.admin.Properties;
import com.nowellpoint.aws.model.data.AbstractDocument;
import com.nowellpoint.aws.model.data.CreateDocumentRequest;
import com.nowellpoint.aws.model.data.CreateDocumentResponse;
import com.nowellpoint.aws.model.data.DeleteDocumentRequest;
import com.nowellpoint.aws.model.data.DeleteDocumentResponse;
import com.nowellpoint.aws.model.data.UpdateDocumentRequest;
import com.nowellpoint.aws.model.data.UpdateDocumentResponse;

public abstract class AbstractDocumentEventHandler implements AbstractEventHandler {
	
	final DataClient dataClient = new DataClient();
	final ObjectMapper mapper = new ObjectMapper();
	
	public String create(String mongoConnectUri, String collectionName, AbstractDocument resource) throws IOException {
		
		//
		//
		//
		
		MongoClientURI mongoClientUri = new MongoClientURI(mongoConnectUri);
		MongoClient mongoClient = new MongoClient(mongoClientUri);
		MongoDatabase mongoDatabase = mongoClient.getDatabase(mongoClientUri.getDatabase());
		
		//
		//
		//
		
		Document document = Document.parse(mapper.writeValueAsString(resource));
		
		if (document.getString("_id") == null) {
			document.put("_id", UUID.randomUUID().toString());
		}
		
		//
		//
		//
		
		try {
			mongoDatabase.getCollection(collectionName).insertOne(document);
		} catch (MongoException e) {
			throw new IOException(e);
		} finally {
			mongoClient.close();
		}
		
		//
		//
		//
		
		return document.getString("_id");
	}
	
	public CreateDocumentResponse createDocument(String mongoClientUri, String collectionName, AbstractDocument document) throws JsonProcessingException {
		
		//
		//
		//
		
		Date now = Date.from(Instant.now());	
		
		//
		//
		//
		
		document.setCreatedDate(now);
		document.setLastModifiedDate(now);
		
		//
		//
		//
		
		CreateDocumentRequest createDocumentRequest = new CreateDocumentRequest()
				.withMongoDBConnectUri(mongoClientUri)
				.withCollectionName(collectionName)
				.withDocument(objectMapper.writeValueAsString(document));
		
		CreateDocumentResponse createDocumentResponse = dataClient.create(createDocumentRequest);
		
		//
		//
		//
		
		return createDocumentResponse;
	}
	
	public UpdateDocumentResponse updateDocument(String mongoClientUri, String collectionName, AbstractDocument document) throws JsonProcessingException {
		
		//
		//
		//
		
		Date now = Date.from(Instant.now());	
		
		//
		//
		//
		
		document.setCreatedDate(now);
		
		//
		//
		//
		
		UpdateDocumentRequest updateDocumentRequest = new UpdateDocumentRequest()
				.withMongoDBConnectUri(mongoClientUri)
				.withCollectionName(collectionName)
				.withDocument(objectMapper.writeValueAsString(document));
		
		UpdateDocumentResponse updateDocumentResponse = dataClient.update(updateDocumentRequest);
		
		//
		//
		//
		
		return updateDocumentResponse;
	}
	
	public DeleteDocumentResponse deleteDocument(String mongoClientUri, String collectionName, AbstractDocument document) {
		
		//
		//
		//
		
		DeleteDocumentRequest deleteDocumentRequest = new DeleteDocumentRequest()
				.withMongoDBConnectUri(mongoClientUri)
				.withCollectionName(collectionName)
				.withId(document.getId());
		
		DeleteDocumentResponse deleteDocumentResponse = dataClient.delete(deleteDocumentRequest);
		
		//
		//
		//
		
		return deleteDocumentResponse;
	}
	
	public void addDocumentToCache(AbstractDocument document, Map<String, String> properties) {
		
		//
		//
		//
		
		String endpoint = properties.get(Properties.REDIS_ENDPOINT);
		Integer port = Integer.valueOf(properties.get(Properties.REDIS_PORT));
		String password = properties.get(Properties.REDIS_PASSWORD);
		
		//
		//
		//
		
		CacheManager cacheManager = new CacheManager(endpoint, port);
		cacheManager.auth(password);
		cacheManager.setex(document.getId(), 259200, document);
		cacheManager.close();
	}
	
	private MongoClientURI getMongoClientUri(String mongoConnectUri) {
		return new MongoClientURI(mongoConnectUri.startsWith("mongodb://") ? mongoConnectUri : "mongodb://".concat(mongoConnectUri));
	}
}