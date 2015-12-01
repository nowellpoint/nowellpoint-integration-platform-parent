package com.nowellpoint.aws.data;

import java.io.IOException;
import java.time.Instant;
import java.util.Date;

import org.apache.http.entity.ContentType;
import org.bson.Document;
import org.bson.types.ObjectId;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.util.json.JSONException;
import com.amazonaws.util.json.JSONObject;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.MongoException;
import com.mongodb.client.MongoDatabase;
import com.nowellpoint.aws.http.HttpRequest;
import com.nowellpoint.aws.http.HttpResponse;
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

		//MongoClientURI mongoClientURI = new MongoClientURI(request.getUserContext().getMongoDBConnectUri());
		
		//logger.log(Instant.now() + " " + context.getAwsRequestId() + " mongo client uri parse time: " + (System.currentTimeMillis() - startTime));
		
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
		
		logger.log(Instant.now() + " " + context.getAwsRequestId() + " creating document in:" + request.getCollectionName());
		
		/**
		 * 
		 */
		
		Date now = Date.from(Instant.now());
		ObjectId userId = new ObjectId(request.getUserContext().getUserId());		
		
		Document document = Document.parse(request.getDocument());
		document.put("createdDate", now);
		document.put("lastModifiedDate", now);
		document.put("createdById", userId);
		document.put("lastModifiedById", userId);		
		
		HttpRequest httpRequest = RestResource.post("https://api.mongolab.com/api/1/databases")
				.path("5567cfcdef8635a007c83688")
				.path("collections")
				.path(request.getCollectionName().concat("?apiKey=").concat("0aGhiuDBE6NOFwvfPSxM2FjVGxg7N_o6"))
				.contentType(ContentType.APPLICATION_JSON.toString())
				.body(document.toJson());
		
		try {
			HttpResponse httpResponse = httpRequest.execute();
			response.setStatusCode(httpResponse.getStatusCode());
			response.setId(Document.parse(httpResponse.getEntity()).getObjectId("_id").toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
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