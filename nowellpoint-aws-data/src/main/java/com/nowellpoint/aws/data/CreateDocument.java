package com.nowellpoint.aws.data;

import java.time.Instant;
import java.util.Date;

import org.bson.Document;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.MongoException;
import com.mongodb.client.MongoDatabase;
import com.nowellpoint.aws.model.Configuration;
import com.nowellpoint.aws.model.data.CreateDocumentRequest;
import com.nowellpoint.aws.model.data.CreateDocumentResponse;

public class CreateDocument implements RequestHandler<CreateDocumentRequest, CreateDocumentResponse> {

	@Override
	public CreateDocumentResponse handleRequest(CreateDocumentRequest request, Context context) {
		
		/**
		 * 
		 */
		
		LambdaLogger logger = context.getLogger();
		
		/**
		 * 
		 */
		
		long startTime = System.currentTimeMillis();

		/**
		 * 
		 */

		MongoClientURI mongoClientURI = new MongoClientURI("mongodb://".concat(Configuration.getMongoClientUri()));
		
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
		
		logger.log(Instant.now() + " " + context.getAwsRequestId() + " creating document in:" + request.getCollectionName());
		
		/**
		 * 
		 */
		
		Date now = Date.from(Instant.now());
		
		Document document = Document.parse(request.getDocument());
		document.put("createdDate", now);
		document.put("lastModifiedDate", now);
		
		try{
			mongoDatabase.getCollection(request.getCollectionName()).insertOne(document);
			response.setStatusCode(201);
			response.setId(document.getObjectId("_id").toString());
		} catch (MongoException e) {
			response.setStatusCode(500);
			response.setErrorCode("unexpected_exception");
			response.setErrorMessage(e.getMessage());
			e.printStackTrace();
		} finally {
			mongoClient.close();
		}
		
		logger.log(Instant.now() + " " + context.getAwsRequestId() + " execution time: " + (System.currentTimeMillis() - startTime));
		
		/**
		 * 
		 */
		
		return response;
	}
}