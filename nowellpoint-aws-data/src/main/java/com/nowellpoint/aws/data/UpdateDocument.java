package com.nowellpoint.aws.data;

import java.time.Instant;
import java.util.Date;
import java.util.logging.Logger;

import org.bson.Document;
import org.bson.types.ObjectId;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.MongoException;
import com.mongodb.client.MongoDatabase;
import com.nowellpoint.aws.model.Configuration;
import com.nowellpoint.aws.model.data.UpdateDocumentRequest;
import com.nowellpoint.aws.model.data.UpdateDocumentResponse;

public class UpdateDocument implements RequestHandler<UpdateDocumentRequest, UpdateDocumentResponse> {
	
	private static final Logger log = Logger.getLogger(UpdateDocument.class.getName());

	@Override
	public UpdateDocumentResponse handleRequest(UpdateDocumentRequest request, Context context) {
		
		/**
		 * 
		 */
		
		long startTime = System.currentTimeMillis();
		
		/**
		 * 
		 */
		
		UpdateDocumentResponse response = new UpdateDocumentResponse();
		
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
		
		Date now = Date.from(Instant.now());
		
		Document document = Document.parse(request.getDocument());
		document.put("_id", new ObjectId(request.getId()));
		document.put("lastModifiedDate", now);
		
		try{
			mongoDatabase.getCollection(request.getCollectionName()).findOneAndReplace(new Document("_id", document.getObjectId("_id")), document);
		} catch (MongoException e) {
			e.printStackTrace();
		}
		
		log.info(document.getObjectId("_id").toString());
		
		log.info("execution time: " + (System.currentTimeMillis() - startTime));
		
		response.setStatusCode(200);
		response.setId(document.getObjectId("_id").toString());
		response.setDocument(document.toJson());
		
		mongoClient.close();
		
		return response;
	}
}