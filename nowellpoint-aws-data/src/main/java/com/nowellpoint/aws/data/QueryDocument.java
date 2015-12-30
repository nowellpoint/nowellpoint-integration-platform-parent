package com.nowellpoint.aws.data;

import java.io.IOException;
import java.time.Instant;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.mongodb.MongoClientURI;
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
		
		QueryDocumentResponse response = new QueryDocumentResponse();

		/**
		 * 
		 */
		
		logger.log(Instant.now() + " " + context.getAwsRequestId() + " querying document in: " + request.getCollectionName());
		
		/**
		 * 
		 */
		
		String queryPath = request.getCollectionName()
				.concat("?q=")
				.concat(request.getDocument())
				.concat("&apiKey=")
				.concat("0aGhiuDBE6NOFwvfPSxM2FjVGxg7N_o6");
		
		HttpRequest httpRequest = RestResource.get("https://api.mongolab.com/api/1/databases")
				.path(mongoClientURI.getDatabase())
				.path("collections")
				.path(queryPath)
				.accept(MediaType.APPLICATION_JSON);
		
		try {
			HttpResponse httpResponse = httpRequest.execute();
			
			logger.log("status code: " + httpResponse.getStatusCode());
			logger.log("url: " + httpResponse.getURL());
			
			ArrayNode results = httpResponse.getEntity(ArrayNode.class);
			response.setStatusCode(200);
			response.setDocument(results.toString());
			response.setCount(results.size());
			
		} catch (IOException e) {
			response.setStatusCode(500);
			response.setErrorCode("unexpected_exception");
			response.setErrorMessage(e.getMessage());
			e.printStackTrace();
		}
		
		logger.log(Instant.now() + " " + context.getAwsRequestId() + " execution time: " + (System.currentTimeMillis() - startTime));
		
		/**
		 * 
		 */
		
		return response;
	}
}