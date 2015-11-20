package com.nowellpoint.aws.data;

import java.util.logging.Logger;

import org.bson.Document;

import static com.mongodb.client.model.Updates.currentTimestamp;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.MongoException;
import com.mongodb.client.MongoDatabase;
import com.nowellpoint.aws.model.Configuration;
import com.nowellpoint.aws.model.data.CreateDocumentRequest;
import com.nowellpoint.aws.model.data.CreateDocumentResponse;

public class CreateDocument implements RequestHandler<CreateDocumentRequest, CreateDocumentResponse> {
	
	private static final Logger log = Logger.getLogger(CreateDocument.class.getName());

	@Override
	public CreateDocumentResponse handleRequest(CreateDocumentRequest request, Context context) {
		
		CreateDocumentResponse response = new CreateDocumentResponse();
		
		long start = System.currentTimeMillis();
		
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
		
		log.info(request.getCollectionName());
		
		Document document = Document.parse(request.getDocument());
		
		try{
		mongoDatabase.getCollection(request.getCollectionName()).insertOne(document);
		} catch (MongoException e) {
			e.printStackTrace();
		}
		
		log.info(document.getObjectId("_id").toString());
		
		log.info("execution time: " + (System.currentTimeMillis() - start));
		
		response.setStatusCode(200);
		response.setId(document.getObjectId("_id").toString());
		
		mongoClient.close();
		
		return response;
	}
}