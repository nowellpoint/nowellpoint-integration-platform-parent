package com.nowellpoint.aws.data;

import java.io.IOException;
import java.time.Instant;
import java.util.Date;

import org.bson.Document;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.mongodb.MongoClientURI;
import com.nowellpoint.aws.http.HttpRequest;
import com.nowellpoint.aws.http.HttpResponse;
import com.nowellpoint.aws.http.MediaType;
import com.nowellpoint.aws.http.RestResource;
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

		MongoClientURI mongoClientURI = new MongoClientURI(request.getMongoDBConnectUri().startsWith("mongodb://") ? request.getMongoDBConnectUri() : "mongodb://".concat(request.getMongoDBConnectUri()));
		
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
		
		Date now = Date.from(Instant.now());	
		
		Document document = Document.parse(request.getDocument());
		document.put("createdDate", now);
		document.put("lastModifiedDate", now);
		document.put("createdById", request.getUserId());
		document.put("lastModifiedById", request.getUserId());		
		
		HttpRequest httpRequest = RestResource.post("https://api.mongolab.com/api/1/databases")
				.path(mongoClientURI.getDatabase())
				.path("collections")
				.path(request.getCollectionName().concat("?apiKey=").concat("0aGhiuDBE6NOFwvfPSxM2FjVGxg7N_o6"))
				.contentType(MediaType.APPLICATION_JSON)
				.body(document.toJson());
		
		try {
			HttpResponse httpResponse = httpRequest.execute();
			
			logger.log("status code: " + httpResponse.getStatusCode());
			
			if (httpResponse.getStatusCode() == 200) {
				response.setStatusCode(201);
				response.setId(Document.parse(httpResponse.getEntity()).getString("_id"));
			} else {
				response.setStatusCode(httpResponse.getStatusCode());
				response.setErrorMessage(httpResponse.getEntity());				
			}
		} catch (IOException e) {
			response.setStatusCode(500);
			response.setErrorCode("unexpected_exception");
			response.setErrorMessage(e.getMessage());
			e.printStackTrace();
		}
		
		/**
		 * 
		 */
		
		return response;
	}
}