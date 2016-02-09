package com.nowellpoint.aws.event;

import java.io.IOException;
import java.util.UUID;

import org.bson.Document;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.MongoException;
import com.mongodb.client.MongoDatabase;
import com.nowellpoint.aws.client.DataClient;
import com.nowellpoint.aws.model.data.AbstractDocument;
import com.nowellpoint.aws.model.data.CreateDocumentRequest;
import com.nowellpoint.aws.model.data.CreateDocumentResponse;
import com.nowellpoint.aws.model.data.DeleteDocumentRequest;
import com.nowellpoint.aws.model.data.DeleteDocumentResponse;
import com.nowellpoint.aws.model.data.QueryDocumentRequest;
import com.nowellpoint.aws.model.data.QueryDocumentResponse;
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
	
	public QueryDocumentResponse queryDocument(String mongoClientUri, String collectionName, String query) {
		
		QueryDocumentRequest queryDocumentRequest = new QueryDocumentRequest()
				.withMongoDBConnectUri(mongoClientUri)
				.withCollectionName(collectionName)				
				.withQuery(query);
		
		QueryDocumentResponse queryDocumentResponse = dataClient.query(queryDocumentRequest);
		
		return queryDocumentResponse;
	}
	
	public CreateDocumentResponse createDocument(String mongoClientUri, String collectionName, AbstractDocument document) throws JsonProcessingException {
		
		CreateDocumentRequest createDocumentRequest = new CreateDocumentRequest()
				.withMongoDBConnectUri(mongoClientUri)
				.withCollectionName(collectionName)
				.withDocument(objectMapper.writeValueAsString(document));
		
		CreateDocumentResponse createDocumentResponse = dataClient.create(createDocumentRequest);
		
		return createDocumentResponse;
	}
	
	public UpdateDocumentResponse updateDocument(String mongoClientUri, String collectionName, AbstractDocument document) throws JsonProcessingException {
				
		UpdateDocumentRequest updateDocumentRequest = new UpdateDocumentRequest()
				.withMongoDBConnectUri(mongoClientUri)
				.withCollectionName(collectionName)
				.withDocument(objectMapper.writeValueAsString(document));
		
		UpdateDocumentResponse updateDocumentResponse = dataClient.update(updateDocumentRequest);
		
		return updateDocumentResponse;
	}
	
	public DeleteDocumentResponse deleteDocument(String mongoClientUri, String collectionName, AbstractDocument document) {
		
		DeleteDocumentRequest deleteDocumentRequest = new DeleteDocumentRequest()
				.withMongoDBConnectUri(mongoClientUri)
				.withCollectionName(collectionName)
				.withId(document.getId());
		
		DeleteDocumentResponse deleteDocumentResponse = dataClient.delete(deleteDocumentRequest);
		
		return deleteDocumentResponse;
	}
}