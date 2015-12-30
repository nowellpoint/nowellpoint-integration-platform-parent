package com.nowellpoint.aws.data;

import java.util.logging.Logger;

import org.bson.types.ObjectId;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.MongoException;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.result.DeleteResult;
import com.nowellpoint.aws.model.data.DeleteDocumentRequest;
import com.nowellpoint.aws.model.data.DeleteDocumentResponse;

public class DeleteDocument implements RequestHandler<DeleteDocumentRequest, DeleteDocumentResponse> {
	
	private static final Logger log = Logger.getLogger(DeleteDocument.class.getName());

	@Override
	public DeleteDocumentResponse handleRequest(DeleteDocumentRequest request, Context context) {
		
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
		
		DeleteDocumentResponse response = new DeleteDocumentResponse();
		
		/**
		 * 
		 */
		
		try{
			DeleteResult result = mongoDatabase.getCollection(request.getCollectionName()).deleteOne( Filters.eq ( "_id", new ObjectId( request.getId() ) ) );
			if (result.getDeletedCount() == 1) {
				response.setStatusCode(200);
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