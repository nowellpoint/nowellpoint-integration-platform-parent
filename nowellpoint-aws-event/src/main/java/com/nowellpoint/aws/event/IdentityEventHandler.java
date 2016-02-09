package com.nowellpoint.aws.event;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.fasterxml.jackson.core.type.TypeReference;
import com.nowellpoint.aws.model.Event;
import com.nowellpoint.aws.model.EventAction;
import com.nowellpoint.aws.model.admin.Properties;
import com.nowellpoint.aws.model.data.CreateDocumentResponse;
import com.nowellpoint.aws.model.data.DeleteDocumentResponse;
import com.nowellpoint.aws.model.data.Identity;
import com.nowellpoint.aws.model.data.QueryDocumentResponse;
import com.nowellpoint.aws.model.data.UpdateDocumentResponse;

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
		
		Identity identity = objectMapper.readValue(event.getPayload(), Identity.class);	
		
		//
		//
		//
		
		String mongoClientUri = properties.get(Properties.MONGO_CLIENT_URI);
		
		//
		//
		//
		
		if (EventAction.SIGN_UP.name().equals(event.getEventAction())) {
			
			String query = objectMapper.createObjectNode().put("username", identity.getUsername()).toString();
			
			QueryDocumentResponse queryDocumentResponse = queryDocument(mongoClientUri, COLLECTION_NAME, query);
			
			logger.log(this.getClass().getName() + " Check user already exists? " + (queryDocumentResponse.getCount() > 0 ? Boolean.TRUE : Boolean.FALSE));
			
			if (queryDocumentResponse.getCount() == 0) {
				
				logger.log(this.getClass().getName() + " Creating identity for account..." + event.getSubject());
					
				CreateDocumentResponse createDocumentResponse = createDocument(mongoClientUri, COLLECTION_NAME, identity);
				
				if (createDocumentResponse.getStatusCode() == 201) {
					identity.setId(createDocumentResponse.getId());
					logger.log(this.getClass().getName() + "Created Identity Id: " + identity.getId());
				} else {
					logger.log( String.format( "%s Failed to create Identity Id: %s: %s", this.getClass().getName(), identity.getId(), createDocumentResponse.getErrorMessage() ) );
					throw new IOException(createDocumentResponse.getErrorMessage());
				}
				
			} else {
				
				logger.log(this.getClass().getName() + " Updating identity for account..." + event.getSubject());
				
				List<Identity> identities = objectMapper.readValue(queryDocumentResponse.getQueryResults(), new TypeReference<List<Identity>>(){});
				
				identity.setId(identities.get(0).getId());
				
				UpdateDocumentResponse updateDocumentResponse = updateDocument(mongoClientUri, COLLECTION_NAME, identity);
				
				if (updateDocumentResponse.getStatusCode() == 200) {
					logger.log( String.format( "%s Updated Identity Id: %s", this.getClass().getName(), identity.getId() ) );
				} else {
					logger.log( String.format( "%s Failed to update Identity Id: %s: %s", this.getClass().getName(), identity.getId(), updateDocumentResponse.getErrorMessage() ) );
					throw new IOException(updateDocumentResponse.getErrorMessage());
				}
			}
		} else if (EventAction.CREATE.name().equals(event.getEventAction())) {
			
			logger.log(this.getClass().getName() + " Creating identity..." + event.getSubject());
			
			CreateDocumentResponse createDocumentResponse = createDocument(mongoClientUri, COLLECTION_NAME, identity);
			
			if (createDocumentResponse.getStatusCode() == 201) {
				identity.setId(createDocumentResponse.getId());
				logger.log(this.getClass().getName() + "Created Identity Id: " + identity.getId());
			} else {
				logger.log( String.format( "%s Failed to create Identity Id: %s: %s", this.getClass().getName(), identity.getId(), createDocumentResponse.getErrorMessage() ) );
				throw new IOException(createDocumentResponse.getErrorMessage());
			}
			
		} else if (EventAction.UPDATE.name().equals(event.getEventAction())) {
			
			UpdateDocumentResponse updateDocumentResponse = updateDocument(mongoClientUri, COLLECTION_NAME, identity);
			
			if (updateDocumentResponse.getStatusCode() == 200) {
				logger.log( String.format( "%s Updated Identity Id: %s", this.getClass().getName(), identity.getId() ) );
			} else {
				logger.log( String.format( "%s Failed to update Identity Id: %s: %s", this.getClass().getName(), identity.getId(), updateDocumentResponse.getErrorMessage() ) );
				throw new IOException(updateDocumentResponse.getErrorMessage());
			}
			
		} else if (EventAction.DELETE.name().equals(event.getEventAction())) {
			
			DeleteDocumentResponse deleteDocumentReponse = deleteDocument(mongoClientUri, COLLECTION_NAME, identity);
			
			if (deleteDocumentReponse.getStatusCode() == 204) {
				logger.log( String.format( "%s Deleted Identity Id: %s", this.getClass().getName(), identity.getId() ) );
			} else {
				logger.log( String.format( "%s Failed to delete Identity Id: %s: %s", this.getClass().getName(), identity.getId(), deleteDocumentReponse.getErrorMessage() ) );
				throw new IOException(deleteDocumentReponse.getErrorMessage());
			}
			
		} else {
			throw new Exception( String.format( "Invalid action for Identity object: %s", event.getEventAction() ) );
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