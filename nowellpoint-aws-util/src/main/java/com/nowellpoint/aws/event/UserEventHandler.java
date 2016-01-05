package com.nowellpoint.aws.event;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.fasterxml.jackson.core.type.TypeReference;
import com.nowellpoint.aws.client.DataClient;
import com.nowellpoint.aws.model.Event;
import com.nowellpoint.aws.model.data.CreateDocumentRequest;
import com.nowellpoint.aws.model.data.CreateDocumentResponse;
import com.nowellpoint.aws.model.data.UpdateDocumentRequest;
import com.nowellpoint.aws.model.data.UpdateDocumentResponse;
import com.nowellpoint.aws.model.data.QueryDocumentRequest;
import com.nowellpoint.aws.model.data.QueryDocumentResponse;
import com.nowellpoint.aws.model.data.User;
import com.nowellpoint.aws.provider.ConfigurationProvider;

public class UserEventHandler implements AbstractEventHandler {
	
	private static final String USER = "users";

	@Override
	public void process(Event event, Context context) throws IOException {
		
		LambdaLogger logger = context.getLogger();
		
		logger.log(new Date() + " starting UserEventHandler");
		
		final DataClient dataClient = new DataClient();
		
		User user = objectMapper.readValue(event.getPayload(), User.class);
		
		String query = objectMapper.createObjectNode().put("username", user.getUsername()).toString();
		
		QueryDocumentRequest queryDocumentRequest = new QueryDocumentRequest().withCollectionName(USER)
				.withMongoDBConnectUri(ConfigurationProvider.getMongoClientUri())
				.withDocument(query);
		
		QueryDocumentResponse queryDocumentResponse = dataClient.query(queryDocumentRequest);
		
		logger.log(new Date() + " user already exists? " + (queryDocumentResponse.getCount() > 0 ? Boolean.TRUE : Boolean.FALSE));
		
		if (queryDocumentResponse.getCount() == 0) {
			
			logger.log(new Date() + " creating user...");
			
			user.setId(new ObjectId());
					
			CreateDocumentRequest createDocumentRequest = new CreateDocumentRequest().withMongoDBConnectUri(ConfigurationProvider.getMongoClientUri())
					.withUserId(user.getId().toString())
					.withCollectionName(USER)
					.withDocument(objectMapper.writeValueAsString(user));
				
			CreateDocumentResponse createDocumentResponse = dataClient.create(createDocumentRequest);	
			
			logger.log(new Date() + " Status Code: " + createDocumentResponse.getStatusCode());
			
			if (createDocumentResponse.getStatusCode() == 201) {
				logger.log(new Date() + " Document Id: " + createDocumentResponse.getId());
			} else {
				throw new IOException(createDocumentResponse.getErrorMessage());
			}
			
		} else {
			
			logger.log(new Date() + " updating user...");
			
			try {
				List<User> users = objectMapper.readValue(queryDocumentResponse.getDocument(), new TypeReference<List<User>>(){});
				user.setId(users.get(0).getId());
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			UpdateDocumentRequest updateDocumentRequest = new UpdateDocumentRequest().withMongoDBConnectUri(ConfigurationProvider.getMongoClientUri())
					.withId(user.getId().toString())
					.withUserId(user.getId().toString())
					.withCollectionName(USER)
					.withDocument(objectMapper.writeValueAsString(user));
			
			UpdateDocumentResponse updateDocumentResponse = dataClient.update(updateDocumentRequest);
			
			logger.log(new Date() + " Status Code: " + updateDocumentResponse.getStatusCode());
			
			if (updateDocumentResponse.getStatusCode() == 200) {
				logger.log(new Date() + " Document Id: " + updateDocumentResponse.getId());
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