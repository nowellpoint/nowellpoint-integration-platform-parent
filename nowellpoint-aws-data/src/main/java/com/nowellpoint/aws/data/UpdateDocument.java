package com.nowellpoint.aws.data;

import org.bson.Document;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.MongoException;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.UpdateResult;
import com.nowellpoint.aws.model.data.UpdateDocumentRequest;
import com.nowellpoint.aws.model.data.UpdateDocumentResponse;

public class UpdateDocument implements RequestHandler<UpdateDocumentRequest, UpdateDocumentResponse> {
	
	private static LambdaLogger logger;

	@Override
	public UpdateDocumentResponse handleRequest(UpdateDocumentRequest request, Context context) {
		
		/**
		 * 
		 */
		
		logger = context.getLogger();
		
		/**
		 * 
		 */

		MongoClientURI mongoClientUri = new MongoClientURI(request.getMongoDBConnectUri().startsWith("mongodb://") ? request.getMongoDBConnectUri() : "mongodb://".concat(request.getMongoDBConnectUri()));
		
		/**
		 * 
		 */
		
		MongoClient mongoClient = new MongoClient(mongoClientUri);
				
		/**
		 * 
		 */ 

		MongoDatabase mongoDatabase = mongoClient.getDatabase(mongoClientUri.getDatabase());
		
		/**
		 * 
		 */
		
		UpdateDocumentResponse response = new UpdateDocumentResponse();
		
		/**
		 * 
		 */
		
		logger.log("Updating document in: " + request.getCollectionName());
		
		//
		//
		//
		
		Document document = Document.parse(request.getDocument());
		
		//
		//
		//
		
		try {
			UpdateResult result = mongoDatabase.getCollection(request.getCollectionName()).updateOne(new Document("_id", document.getString("_id")), new Document("$set", document));
			if (result.getModifiedCount() == 1) {
				response.setStatusCode(200);
				response.setId(document.getObjectId("_id"));
			} else {
				response.setStatusCode(404);
				response.setErrorCode("not_found");
				response.setErrorMessage(String.format("Document of type %s for Id: %s was not found", new Object[] {request.getCollectionName(), document.getString("_id")}));
			}
		} catch (MongoException e) {
			response.setStatusCode(500);
			response.setErrorCode("unexpected_exception");
			response.setErrorMessage(e.getMessage());
			e.printStackTrace();
		} finally {
			mongoClient.close();
		}

		return response;
	}
}