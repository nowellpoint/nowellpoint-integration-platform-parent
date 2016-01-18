package com.nowellpoint.aws.event;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.fasterxml.jackson.core.type.TypeReference;
import com.nowellpoint.aws.client.DataClient;
import com.nowellpoint.aws.model.Event;
import com.nowellpoint.aws.model.admin.Properties;
import com.nowellpoint.aws.model.data.CreateDocumentRequest;
import com.nowellpoint.aws.model.data.CreateDocumentResponse;
import com.nowellpoint.aws.model.data.UpdateDocumentRequest;
import com.nowellpoint.aws.model.data.UpdateDocumentResponse;
import com.nowellpoint.aws.model.data.QueryDocumentRequest;
import com.nowellpoint.aws.model.data.QueryDocumentResponse;
import com.nowellpoint.aws.model.data.Identity;

public class IdentityEventHandler implements AbstractEventHandler {
	
	private static final String COLLECTION_NAME = "identities";

	@Override
	public void process(Event event, Context context) throws Exception {
		
		LambdaLogger logger = context.getLogger();
		
		logger.log(new Date() + " starting IdentityEventHandler");
		
		String mongoClientUri = Properties.getProperty(event.getPropertyStore(), Properties.MONGO_CLIENT_URI);
		
		final DataClient dataClient = new DataClient();
		
		Identity user = objectMapper.readValue(event.getPayload(), Identity.class);
		
		String query = objectMapper.createObjectNode().put("username", user.getUsername()).toString();
		
		QueryDocumentRequest queryDocumentRequest = new QueryDocumentRequest().withCollectionName(COLLECTION_NAME)
				.withMongoDBConnectUri(mongoClientUri)
				.withDocument(query);
		
		QueryDocumentResponse queryDocumentResponse = dataClient.query(queryDocumentRequest);
		
		logger.log(new Date() + " user already exists? " + (queryDocumentResponse.getCount() > 0 ? Boolean.TRUE : Boolean.FALSE));
		
		if (queryDocumentResponse.getCount() == 0) {
			
			logger.log("Creating user...");
			
			user.setId(event.getId());
					
			CreateDocumentRequest createDocumentRequest = new CreateDocumentRequest().withMongoDBConnectUri(mongoClientUri)
					.withAccountId(user.getId().toString())
					.withCollectionName(COLLECTION_NAME)
					.withDocument(objectMapper.writeValueAsString(user));
				
			CreateDocumentResponse createDocumentResponse = dataClient.create(createDocumentRequest);	
			
			logger.log(new Date() + " Status Code: " + createDocumentResponse.getStatusCode());
			
			if (createDocumentResponse.getStatusCode() == 201) {
				logger.log("Document Id: " + createDocumentResponse.getId());
			} else {
				throw new IOException(createDocumentResponse.getErrorMessage());
			}
			
		} else {
			
			logger.log(new Date() + " updating user...");
			
			try {
				List<Identity> users = objectMapper.readValue(queryDocumentResponse.getDocument(), new TypeReference<List<Identity>>(){});
				user.setId(users.get(0).getId());
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			UpdateDocumentRequest updateDocumentRequest = new UpdateDocumentRequest().withMongoDBConnectUri(mongoClientUri)
					.withAccountId(user.getId())
					.withCollectionName(COLLECTION_NAME)
					.withDocument(objectMapper.writeValueAsString(user));
			
			UpdateDocumentResponse updateDocumentResponse = dataClient.update(updateDocumentRequest);
			
			logger.log(new Date() + " Status Code: " + updateDocumentResponse.getStatusCode());
			
			if (updateDocumentResponse.getStatusCode() == 200) {
				logger.log("Document Id: " + updateDocumentResponse.getId());
			} else {
				throw new IOException(updateDocumentResponse.getErrorMessage());
			}
		}
		
		//
		//
		//
		
		event.setTargetId(user.getId().toString());		
	}
}