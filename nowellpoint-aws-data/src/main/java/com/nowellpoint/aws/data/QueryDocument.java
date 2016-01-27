package com.nowellpoint.aws.data;

import java.io.IOException;
import java.time.Instant;
import java.util.Optional;

import org.bson.Document;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.MongoException;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.nowellpoint.aws.http.HttpRequest;
import com.nowellpoint.aws.http.HttpResponse;
import com.nowellpoint.aws.http.MediaType;
import com.nowellpoint.aws.http.RestResource;
import com.nowellpoint.aws.model.data.QueryDocumentRequest;
import com.nowellpoint.aws.model.data.QueryDocumentResponse;

public class QueryDocument implements RequestHandler<QueryDocumentRequest, QueryDocumentResponse> {

	@Override
	public QueryDocumentResponse handleRequest(QueryDocumentRequest request, Context context) {
		
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
		
		QueryDocumentResponse response = new QueryDocumentResponse();

		/**
		 * 
		 */
		
		logger.log(Instant.now() + " " + context.getAwsRequestId() + " querying document in: " + request.getCollectionName());
		
		/**
		 * 
		 */
		
		try{
			Optional<FindIterable<Document>> document = Optional.ofNullable(mongoDatabase.getCollection(request.getCollectionName()).find( Document.parse( request.getDocument() ) ) );
			if (document.isPresent()) {
				response.setStatusCode(200);
				response.setDocument(document.get().toString());
				response.setCount(1);
			} else {
				response.setStatusCode(404);
				response.setErrorCode("not_found");
				response.setErrorMessage(String.format("Document of type %s for query: %s was not found", new Object[] {request.getCollectionName(), request.getDocument()}));
			}
		} catch (MongoException e) {
			response.setStatusCode(500);
			response.setErrorCode("unexpected_exception");
			response.setErrorMessage(e.getMessage());
			e.printStackTrace();
		} finally {
			mongoClient.close();
		}
		
		/**
		 * 
		 */
		
		return response;
	}
}