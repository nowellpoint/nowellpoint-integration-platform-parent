package com.nowellpoint.aws.event;

import java.io.IOException;
import java.util.List;
import java.util.Map;

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

public class IdentityEventHandler extends AbstractDocumentEventHandler {
	
	private static final String COLLECTION_NAME = "identities";
	
	private static LambdaLogger logger;

	@Override
	public void process(Event event, Map<String, String> properties, Context context) throws Exception {
		
		//
		//
		//
		
		logger = context.getLogger();
		
		//
		//
		//
		
		logger.log(this.getClass().getName() + " starting IdentityEventHandler");
		
		//
		//
		//
		
		final DataClient dataClient = new DataClient();
		
		Identity identity = objectMapper.readValue(event.getPayload(), Identity.class);	
		
		String query = objectMapper.createObjectNode().put("username", identity.getUsername()).toString();
		
		QueryDocumentRequest queryDocumentRequest = new QueryDocumentRequest()
				.withCollectionName(COLLECTION_NAME)
				.withMongoDBConnectUri(properties.get(Properties.MONGO_CLIENT_URI))
				.withQuery(query);
		
		QueryDocumentResponse queryDocumentResponse = dataClient.query(queryDocumentRequest);
		
		logger.log(this.getClass().getName() + " Check user already exists? " + (queryDocumentResponse.getCount() > 0 ? Boolean.TRUE : Boolean.FALSE));
		
		if (queryDocumentResponse.getCount() == 0) {
			
			logger.log(this.getClass().getName() + " Creating identity for account..." + event.getSubject());
					
			CreateDocumentRequest createDocumentRequest = new CreateDocumentRequest()
					.withMongoDBConnectUri(properties.get(Properties.MONGO_CLIENT_URI))
					.withCollectionName(COLLECTION_NAME)
					.withDocument(objectMapper.writeValueAsString(identity));
				
			CreateDocumentResponse createDocumentResponse = dataClient.create(createDocumentRequest);	
			
			logger.log(this.getClass().getName() + " Status Code: " + createDocumentResponse.getStatusCode());
			
			if (createDocumentResponse.getStatusCode() == 201) {
				logger.log(this.getClass().getName() + " Document Id: " + createDocumentResponse.getId());
				identity.setId(createDocumentResponse.getId());
			} else {
				throw new IOException(createDocumentResponse.getErrorMessage());
			}
			
		} else {
			
			logger.log(this.getClass().getName() + " Updating identity for account..." + event.getSubject());
			
			List<Identity> identities = objectMapper.readValue(queryDocumentResponse.getQueryResults(), new TypeReference<List<Identity>>(){});
			
			identity.setId(identities.get(0).getId());
			
			UpdateDocumentRequest updateDocumentRequest = new UpdateDocumentRequest()
					.withMongoDBConnectUri(properties.get(Properties.MONGO_CLIENT_URI))
					.withCollectionName(COLLECTION_NAME)
					.withDocument(objectMapper.writeValueAsString(identity));
			
			UpdateDocumentResponse updateDocumentResponse = dataClient.update(updateDocumentRequest);
			
			logger.log(this.getClass().getName() + " Status Code: " + updateDocumentResponse.getStatusCode());
			
			if (updateDocumentResponse.getStatusCode() == 200) {
				logger.log(this.getClass().getName() + " Document Id: " + updateDocumentResponse.getId());
				identity.setId(updateDocumentResponse.getId());
			} else {
				throw new IOException(updateDocumentResponse.getErrorMessage());
			}
		}
		
		//
		//
		//
		
		logger.log("identity: " + identity.getId());
		
		//
		//
		//
		
		event.setTargetId(identity.getId());		
	}
}