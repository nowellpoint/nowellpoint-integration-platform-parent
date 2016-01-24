package com.nowellpoint.aws.event;

import java.io.IOException;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.fasterxml.jackson.core.type.TypeReference;
import com.nowellpoint.aws.CacheManager;
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
		
		//
		//
		//
		
		LambdaLogger logger = context.getLogger();
		
		//
		//
		//
		
		logger.log(new Date() + " starting IdentityEventHandler");
		
		//
		//
		//
		
		Map<String, String> properties = Properties.getProperties(event.getPropertyStore());
		
		//
		//
		//
		
		final DataClient dataClient = new DataClient();
		
		Identity identity = objectMapper.readValue(event.getPayload(), Identity.class);
		
		Date now = Date.from(Instant.now());	
		
		String query = objectMapper.createObjectNode().put("username", identity.getUsername()).toString();
		
		QueryDocumentRequest queryDocumentRequest = new QueryDocumentRequest()
				.withCollectionName(COLLECTION_NAME)
				.withMongoDBConnectUri(properties.get(Properties.MONGO_CLIENT_URI))
				.withDocument(query);
		
		QueryDocumentResponse queryDocumentResponse = dataClient.query(queryDocumentRequest);
		
		logger.log("Check user already exists? " + (queryDocumentResponse.getCount() > 0 ? Boolean.TRUE : Boolean.FALSE));
		
		if (queryDocumentResponse.getCount() == 0) {
			
			identity.setId(event.getId());
			identity.setAccountHref(event.getAccountId());
			identity.setCreatedById(event.getAccountId());
			identity.setLastModifiedById(event.getAccountId());
			identity.setCreatedDate(now);
			identity.setLastModifiedDate(now);
			
			logger.log("Creating identity for account..." + event.getAccountId());
					
			CreateDocumentRequest createDocumentRequest = new CreateDocumentRequest()
					.withMongoDBConnectUri(properties.get(Properties.MONGO_CLIENT_URI))
					.withCollectionName(COLLECTION_NAME)
					.withDocument(objectMapper.writeValueAsString(identity));
				
			CreateDocumentResponse createDocumentResponse = dataClient.create(createDocumentRequest);	
			
			logger.log("Status Code: " + createDocumentResponse.getStatusCode());
			
			if (createDocumentResponse.getStatusCode() == 201) {
				logger.log("Document Id: " + createDocumentResponse.getId());
				identity.setId(createDocumentResponse.getId());
			} else {
				throw new IOException(createDocumentResponse.getErrorMessage());
			}
			
		} else {
			
			logger.log("Updating identity for account..." + event.getAccountId());
			
			List<Identity> identities = objectMapper.readValue(queryDocumentResponse.getDocument(), new TypeReference<List<Identity>>(){});
			
			identity.setId(identities.get(0).getId());
			identity.setLastModifiedById(event.getAccountId());
			identity.setLastModifiedDate(now);
			
			UpdateDocumentRequest updateDocumentRequest = new UpdateDocumentRequest()
					.withMongoDBConnectUri(properties.get(Properties.MONGO_CLIENT_URI))
					.withCollectionName(COLLECTION_NAME)
					.withDocument(objectMapper.writeValueAsString(identity));
			
			UpdateDocumentResponse updateDocumentResponse = dataClient.update(updateDocumentRequest);
			
			logger.log("Status Code: " + updateDocumentResponse.getStatusCode());
			
			if (updateDocumentResponse.getStatusCode() == 200) {
				logger.log("Document Id: " + updateDocumentResponse.getId());
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
		
		CacheManager cacheProvider = new CacheManager();
		cacheProvider.auth(properties.get(Properties.REDIS_PASSWORD));
		cacheProvider.setex(identity.getId(), 259200, identity);
		cacheProvider.close();
		
		//
		//
		//
		
		event.setTargetId(identity.getId());		
	}
}