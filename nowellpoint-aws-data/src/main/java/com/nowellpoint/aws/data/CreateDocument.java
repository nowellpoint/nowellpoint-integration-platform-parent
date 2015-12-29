package com.nowellpoint.aws.data;

import java.io.IOException;
import java.time.Instant;
import java.util.Date;

import org.bson.Document;
import org.bson.types.ObjectId;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
//import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
//import com.mongodb.MongoException;
//import com.mongodb.client.MongoDatabase;
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
		
		long startTime = System.currentTimeMillis();

		/**
		 * 
		 */

		MongoClientURI mongoClientURI = new MongoClientURI(request.getMongoDBConnectUri().startsWith("mongodb://") ? request.getMongoDBConnectUri() : "mongodb://".concat(request.getMongoDBConnectUri()));
		
		/**
		 * 
		 */
		
		//MongoClient mongoClient = new MongoClient(mongoClientURI);
				
		/**
		 * 
		 */ 

		//MongoDatabase mongoDatabase = mongoClient.getDatabase(mongoClientURI.getDatabase());
		
		/**
		 * 
		 */
		
		CreateDocumentResponse response = new CreateDocumentResponse();

		/**
		 * 
		 */
		
		logger.log(Instant.now() + " " + context.getAwsRequestId() + " creating document in: " + request.getCollectionName());
		
		/**
		 * 
		 */
		
		Date now = Date.from(Instant.now());

		ObjectId userId = new ObjectId(request.getUserId());		
		
		Document document = Document.parse(request.getDocument());
		document.put("createdDate", now);
		document.put("lastModifiedDate", now);
		document.put("createdById", userId);
		document.put("lastModifiedById", userId);		
		
		if (document.getString("_id") != null) {
			document.put("_id", new ObjectId(document.getString("_id")));
		}
		
		HttpRequest httpRequest = RestResource.post("https://api.mongolab.com/api/1/databases")
				.path(mongoClientURI.getDatabase())
				.path("collections")
				.path(request.getCollectionName().concat("?apiKey=").concat("0aGhiuDBE6NOFwvfPSxM2FjVGxg7N_o6"))
				.contentType(MediaType.APPLICATION_JSON)
				.body(document.toJson());
		
		try {
			HttpResponse httpResponse = httpRequest.execute();			
			if (httpResponse.getStatusCode() == 200) {
				response.setStatusCode(201);
				response.setId(Document.parse(httpResponse.getEntity()).getObjectId("_id").toString());
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
		

		
//		try{
//			mongoDatabase.getCollection(request.getCollectionName()).insertOne(document);
//			response.setStatusCode(201);
//			response.setId(document.getObjectId("_id").toString());
//		} catch (MongoException e) {
//			response.setStatusCode(500);
//			response.setErrorCode("unexpected_exception");
//			response.setErrorMessage(e.getMessage());
//			e.printStackTrace();
//		} finally {
//			mongoClient.close();
//		}
		
		logger.log(Instant.now() + " " + context.getAwsRequestId() + " execution time: " + (System.currentTimeMillis() - startTime));
		
		/**
		 * 
		 */
		
		return response;
	}
}