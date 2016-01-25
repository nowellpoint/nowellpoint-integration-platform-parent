package com.nowellpoint.aws.event;

import java.io.IOException;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.nowellpoint.aws.client.DataClient;
import com.nowellpoint.aws.model.Event;
import com.nowellpoint.aws.model.admin.Properties;
import com.nowellpoint.aws.model.data.CreateDocumentRequest;
import com.nowellpoint.aws.model.data.CreateDocumentResponse;
import com.nowellpoint.aws.model.data.Organization;
import com.nowellpoint.aws.model.data.UpdateDocumentRequest;
import com.nowellpoint.aws.model.data.UpdateDocumentResponse;

public class OrganizationEventHandler implements AbstractEventHandler {
	
	private static final String COLLECTION = "organizations";
	
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
		
		logger.log(this.getClass().getName() + " starting OrganizationEventHandler");
		
		//
		//
		//
		
		String mongoClientUri = properties.get(Properties.MONGO_CLIENT_URI);
		
		final DataClient dataClient = new DataClient();
		
		Date now = Date.from(Instant.now());	
		
		Organization organization = objectMapper.readValue(event.getPayload(), Organization.class);
	
		if (organization.getId() != null) {
			
			organization.setLastModifiedById(event.getSubjectId());
			organization.setLastModifiedDate(now);
			
			UpdateDocumentRequest updateDocumentRequest = new UpdateDocumentRequest()
					.withMongoDBConnectUri(mongoClientUri)
					.withCollectionName(COLLECTION)
					.withDocument(objectMapper.writeValueAsString(organization));
			
			UpdateDocumentResponse updateDocumentResponse = dataClient.update(updateDocumentRequest);
			
			logger.log(this.getClass().getName() + " Update Document Status Code: " + updateDocumentResponse.getStatusCode());
			
			if (updateDocumentResponse.getStatusCode() == 200) {
				logger.log(this.getClass().getName() + " Document Id: " + updateDocumentResponse.getId());
			} else {
				throw new IOException(updateDocumentResponse.getErrorMessage());
			}
			
		} else {
			
			organization.setId(event.getId());
			organization.setCreatedById(event.getSubjectId());
			organization.setLastModifiedById(event.getSubjectId());
			organization.setCreatedDate(now);
			organization.setLastModifiedDate(now);
			
			CreateDocumentRequest createDocumentRequest = new CreateDocumentRequest()
					.withMongoDBConnectUri(mongoClientUri)
					.withCollectionName(COLLECTION)
					.withDocument(objectMapper.writeValueAsString(organization));
				
			CreateDocumentResponse createDocumentResponse = dataClient.create(createDocumentRequest);	
			
			logger.log(this.getClass().getName() + " Create Document Status Code: " + createDocumentResponse.getStatusCode());
			
			if (createDocumentResponse.getStatusCode() == 201) {
				logger.log(this.getClass().getName() + " Document Id: " + createDocumentResponse.getId());
			} else {
				throw new IOException(createDocumentResponse.getErrorMessage());
			}
		}
	}
}