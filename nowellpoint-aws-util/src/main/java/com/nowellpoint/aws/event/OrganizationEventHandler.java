package com.nowellpoint.aws.event;

import java.io.IOException;
import java.util.Date;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.nowellpoint.aws.client.DataClient;
import com.nowellpoint.aws.model.Event;
import com.nowellpoint.aws.model.admin.Configuration;
import com.nowellpoint.aws.model.data.CreateDocumentRequest;
import com.nowellpoint.aws.model.data.CreateDocumentResponse;
import com.nowellpoint.aws.model.data.Organization;
import com.nowellpoint.aws.model.data.UpdateDocumentRequest;
import com.nowellpoint.aws.model.data.UpdateDocumentResponse;
import com.nowellpoint.aws.provider.ConfigurationProvider;

public class OrganizationEventHandler implements AbstractEventHandler {
	
	private static final String COLLECTION = "organizations";

	@Override
	public void process(Event event, Context context) throws IOException {
		
		LambdaLogger logger = context.getLogger(); 
		
		logger.log(new Date() + " starting OrganizationEventHandler");
		
		Configuration configuration = ConfigurationProvider.getConfiguration(event.getConfigurationId());
		
		final DataClient dataClient = new DataClient();
		
		Organization organization = objectMapper.readValue(event.getPayload(), Organization.class);
	
		if (organization.getId() != null) {
			
			UpdateDocumentRequest updateDocumentRequest = new UpdateDocumentRequest().withMongoDBConnectUri(configuration.getMongoClientUri())
					.withId(organization.getId().toString())
					.withUserId(event.getUserId())
					.withCollectionName(COLLECTION)
					.withDocument(objectMapper.writeValueAsString(organization));
			
			UpdateDocumentResponse updateDocumentResponse = dataClient.update(updateDocumentRequest);
			
			logger.log(new Date() +" Update Document Status Code: " + updateDocumentResponse.getStatusCode());
			
			if (updateDocumentResponse.getStatusCode() == 200) {
				logger.log(new Date() + " Document Id: " + updateDocumentResponse.getId());
			} else {
				throw new IOException(updateDocumentResponse.getErrorMessage());
			}
			
		} else {
			
			organization.setId(event.getId());
			
			CreateDocumentRequest createDocumentRequest = new CreateDocumentRequest().withMongoDBConnectUri(configuration.getMongoClientUri())
					.withUserId(event.getUserId())
					.withCollectionName(COLLECTION)
					.withDocument(objectMapper.writeValueAsString(organization));
				
			CreateDocumentResponse createDocumentResponse = dataClient.create(createDocumentRequest);	
			
			logger.log(new Date() + " Create Document Status Code: " + createDocumentResponse.getStatusCode());
			
			if (createDocumentResponse.getStatusCode() == 201) {
				logger.log(new Date() + " Document Id: " + createDocumentResponse.getId());
			} else {
				throw new IOException(createDocumentResponse.getErrorMessage());
			}
		}
	}
}