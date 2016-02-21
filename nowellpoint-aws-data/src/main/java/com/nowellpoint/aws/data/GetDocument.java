package com.nowellpoint.aws.data;

import java.util.Optional;
import java.util.logging.Logger;

import org.bson.Document;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.MongoException;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.nowellpoint.aws.model.data.GetDocumentRequest;
import com.nowellpoint.aws.model.data.GetDocumentResponse;

public class GetDocument implements RequestHandler<GetDocumentRequest, GetDocumentResponse> {
	
	private static final Logger log = Logger.getLogger(GetDocument.class.getName());

	@Override
	public GetDocumentResponse handleRequest(GetDocumentRequest request, Context context) {
		
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
		
		GetDocumentResponse response = new GetDocumentResponse();
		
		/**
		 * 
		 */
		
		try{
			Optional<Document> document = Optional.ofNullable(mongoDatabase.getCollection(request.getCollectionName()).find(Filters.eq ( "_id", request.getId())).first());
			if (document.isPresent()) {
				response.setStatusCode(200);
				response.setId(document.get().getObjectId("_id"));
				response.setDocument(document.get().toJson());
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