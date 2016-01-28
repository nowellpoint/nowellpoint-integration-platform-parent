package com.nowellpoint.aws.data;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.bson.Document;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.MongoException;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoDatabase;
import com.nowellpoint.aws.model.data.QueryDocumentRequest;
import com.nowellpoint.aws.model.data.QueryDocumentResponse;

public class QueryDocument implements RequestHandler<QueryDocumentRequest, QueryDocumentResponse> {
	
	private static LambdaLogger logger;

	@Override
	public QueryDocumentResponse handleRequest(QueryDocumentRequest request, Context context) {
		
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
		
		QueryDocumentResponse response = new QueryDocumentResponse();

		/**
		 * 
		 */
		
		logger.log("Querying document in: " + request.getCollectionName() + " " + request.getQuery());
		
		/**
		 * 
		 */
		
		try{
			FindIterable<Document> iterable = mongoDatabase.getCollection(request.getCollectionName()).find( Document.parse( request.getQuery() ) );
			Set<Document> results = StreamSupport.stream(iterable.spliterator(), false).collect(Collectors.toSet());
			response.setStatusCode(200);
			response.setCount(results.size());
			response.setQueryResults(new ObjectMapper().writeValueAsString(results));
		} catch (MongoException | JsonProcessingException e) {
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