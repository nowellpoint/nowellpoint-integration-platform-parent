package com.nowellpoint.aws.data;

import java.util.UUID;

import org.bson.Document;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.MongoException;
import com.mongodb.client.MongoDatabase;
import com.nowellpoint.aws.model.data.CreateDocumentRequest;
import com.nowellpoint.aws.model.data.CreateDocumentResponse;

public class CreateDocument implements RequestHandler<CreateDocumentRequest, CreateDocumentResponse> {
	
	private static LambdaLogger logger;

	@Override
	public CreateDocumentResponse handleRequest(CreateDocumentRequest request, Context context) {
		
		/**
		 * 
		 */
		
		logger = context.getLogger();

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
		
		CreateDocumentResponse response = new CreateDocumentResponse();

		/**
		 * 
		 */
		
		logger.log("Creating document in: " + request.getCollectionName());
		
		/**
		 * 
		 */	
		
		Document document = Document.parse(request.getDocument());
		
		if (document.getString("_id") == null) {
			document.put("_id", UUID.randomUUID().toString());
		}
		
		//
		//
		//
		
		try{
			mongoDatabase.getCollection(request.getCollectionName()).insertOne(document);
			response.setStatusCode(201);
			response.setId(document.getString("_id"));
		} catch (MongoException e) {
			response.setStatusCode(500);
			response.setErrorCode("unexpected_exception");
			response.setErrorMessage(e.getMessage());
			e.printStackTrace();
		} finally {
			mongoClient.close();
		}
		
//		HttpRequest httpRequest = RestResource.post("https://api.mongolab.com/api/1/databases")
//				.path(mongoClientURI.getDatabase())
//				.path("collections")
//				.path(request.getCollectionName()
//						.concat("?apiKey=")
//						.concat(request.getApiKey()))
//				.contentType(MediaType.APPLICATION_JSON)
//				.body(document.toJson());
//		
//		try {
//			HttpResponse httpResponse = httpRequest.execute();
//			
//			logger.log("status code: " + httpResponse.getStatusCode());
//			
//			if (httpResponse.getStatusCode() == 200) {
//				response.setStatusCode(201);
//				response.setId(Document.parse(httpResponse.getEntity()).getString("_id"));
//			} else {
//				response.setStatusCode(httpResponse.getStatusCode());
//				response.setErrorMessage(httpResponse.getEntity());				
//			}
//		} catch (IOException e) {
//			response.setStatusCode(500);
//			response.setErrorCode("unexpected_exception");
//			response.setErrorMessage(e.getMessage());
//			e.printStackTrace();
//		}
		
		/**
		 * 
		 */
		
		return response;
	}
}