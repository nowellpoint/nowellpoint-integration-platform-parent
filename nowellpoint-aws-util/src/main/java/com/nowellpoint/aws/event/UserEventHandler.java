package com.nowellpoint.aws.event;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

import org.bson.types.ObjectId;

import com.fasterxml.jackson.core.type.TypeReference;
import com.nowellpoint.aws.client.DataClient;
import com.nowellpoint.aws.model.data.CreateDocumentRequest;
import com.nowellpoint.aws.model.data.CreateDocumentResponse;
import com.nowellpoint.aws.model.data.UpdateDocumentRequest;
import com.nowellpoint.aws.model.data.UpdateDocumentResponse;
import com.nowellpoint.aws.model.data.QueryDocumentRequest;
import com.nowellpoint.aws.model.data.QueryDocumentResponse;
import com.nowellpoint.aws.model.data.User;
import com.nowellpoint.aws.provider.ConfigurationProvider;

public class UserEventHandler implements AbstractEventHandler {
	
	private static final Logger log = Logger.getLogger(UserEventHandler.class.getName());
	
	private static final String USER_COLLECTION = "users";

	@Override
	public String process(String payload) throws IOException {
		
		log.info("starting UserEventHandler");
		
		final DataClient dataClient = new DataClient();
		
		User user = objectMapper.readValue(payload, User.class);
		
		String query = objectMapper.createObjectNode().put("username", user.getUsername()).toString();
		
		QueryDocumentRequest queryDocumentRequest = new QueryDocumentRequest().withCollectionName(USER_COLLECTION)
				.withMongoDBConnectUri(ConfigurationProvider.getMongoClientUri())
				.withDocument(query);
		
		QueryDocumentResponse queryDocumentResponse = dataClient.query(queryDocumentRequest);
		
		log.info("user already exists? " + (queryDocumentResponse.getCount() > 0 ? Boolean.TRUE : Boolean.FALSE));
		
		if (queryDocumentResponse.getCount() == 0) {
			
			log.info("creating user...");
			
			user.setId(new ObjectId());
					
			CreateDocumentRequest createDocumentRequest = new CreateDocumentRequest().withMongoDBConnectUri(ConfigurationProvider.getMongoClientUri())
					.withUserId(user.getId().toString())
					.withCollectionName(USER_COLLECTION)
					.withDocument(objectMapper.writeValueAsString(user));
				
			CreateDocumentResponse createDocumentResponse = dataClient.create(createDocumentRequest);	
			
			log.info("Status Code: " + createDocumentResponse.getStatusCode());
			
			if (createDocumentResponse.getStatusCode() == 201) {
				log.info("Document Id: " + createDocumentResponse.getId());
			} else {
				throw new IOException(createDocumentResponse.getErrorMessage());
			}
			
		} else {
			
			log.info("updating user...");
			
			try {
				List<User> users = objectMapper.readValue(queryDocumentResponse.getDocument(), new TypeReference<List<User>>(){});
				user.setId(users.get(0).getId());
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			UpdateDocumentRequest updateDocumentRequest = new UpdateDocumentRequest().withMongoDBConnectUri(ConfigurationProvider.getMongoClientUri())
					.withId(user.getId().toString())
					.withUserId(user.getId().toString())
					.withCollectionName(USER_COLLECTION)
					.withDocument(objectMapper.writeValueAsString(user));
			
			UpdateDocumentResponse updateDocumentResponse = dataClient.update(updateDocumentRequest);
			
			log.info("Status Code: " + updateDocumentResponse.getStatusCode());
			
			if (updateDocumentResponse.getStatusCode() == 200) {
				log.info("Document Id: " + updateDocumentResponse.getId());
			} else {
				throw new IOException(updateDocumentResponse.getErrorMessage());
			}
		}
		
		//
		//
		//
		
		return user.getId().toString();		
	}
}