package com.nowellpoint.aws.data;

import java.time.Instant;
import java.util.Date;
import java.util.logging.Logger;

import org.bson.Document;
import org.bson.types.ObjectId;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.MongoException;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.UpdateResult;
import com.nowellpoint.aws.model.data.UpdateDocumentRequest;
import com.nowellpoint.aws.model.data.UpdateDocumentResponse;

public class UpdateDocument implements RequestHandler<UpdateDocumentRequest, UpdateDocumentResponse> {
	
	private static final Logger log = Logger.getLogger(UpdateDocument.class.getName());

	@Override
	public UpdateDocumentResponse handleRequest(UpdateDocumentRequest request, Context context) {
		
		/**
		 * 
		 */
		
		long startTime = System.currentTimeMillis();
		
		/**
		 * 
		 */

		MongoClientURI mongoClientURI = new MongoClientURI(request.getMongoDBConnectUri().startsWith("mongodb://") ? request.getMongoDBConnectUri() : "mongodb://".concat(request.getMongoDBConnectUri()));
		
		/**
		 * 
		 */
		
		MongoClient mongoClient = new MongoClient(mongoClientURI);
				
		/**
		 * 
		 */ 

		MongoDatabase mongoDatabase = mongoClient.getDatabase(mongoClientURI.getDatabase());
		
		/**
		 * 
		 */
		
		log.info("connection time: " + (System.currentTimeMillis() - startTime));
		
		/**
		 * 
		 */
		
		UpdateDocumentResponse response = new UpdateDocumentResponse();
		
		/**
		 * 
		 */
		
		log.info(request.getCollectionName());
		
		try{
			
			Date now = Date.from(Instant.now());
			ObjectId userId = new ObjectId(request.getUserId());
			
			log.info(request.getDocument());
			
			Document document = Document.parse(request.getDocument());
			document.put("_id", new ObjectId(request.getId()));
			document.put("lastModifiedDate", now);
			document.put("lastModifiedById", userId);
			
			UpdateResult result = mongoDatabase.getCollection(request.getCollectionName()).updateOne(new Document("_id", document.getObjectId("_id")), new Document("$set", document));
			if (result.getModifiedCount() == 1) {
				response.setStatusCode(200);
				response.setId(document.getObjectId("_id").toString());
			} else {
				response.setStatusCode(404);
				response.setErrorCode("not_found");
				response.setErrorMessage(String.format("Document of type %s for Id: %s was not found", new Object[] {request.getCollectionName(), request.getId()}));
			}
		} catch (MongoException e) {
			response.setStatusCode(500);
			response.setErrorCode("unexpected_exception");
			response.setErrorMessage(e.getMessage());
			e.printStackTrace();
		} finally {
			mongoClient.close();
		}
		
		log.info("execution time: " + (System.currentTimeMillis() - startTime));

		return response;
	}
}