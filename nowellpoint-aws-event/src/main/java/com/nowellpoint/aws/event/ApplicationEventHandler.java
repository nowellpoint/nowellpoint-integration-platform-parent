package com.nowellpoint.aws.event;

import java.io.IOException;
import java.util.Map;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.nowellpoint.aws.model.Event;
import com.nowellpoint.aws.model.EventAction;
import com.nowellpoint.aws.model.admin.Properties;
import com.nowellpoint.aws.model.data.Application;
import com.nowellpoint.aws.model.data.CreateDocumentResponse;
import com.nowellpoint.aws.model.data.DeleteDocumentResponse;
import com.nowellpoint.aws.model.data.UpdateDocumentResponse;

public class ApplicationEventHandler extends AbstractDocumentEventHandler {

	private static final String COLLECTION_NAME = "applications";
	
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
		
		logger.log(this.getClass().getName() + " starting ApplicationEventHandler");
		
		//
		//
		//
		
		Application application = objectMapper.readValue(event.getPayload(), Application.class);
		
		//
		//
		//
		
		if (EventAction.CREATE.name().equals(event.getEventAction())) {	
			
			logger.log(this.getClass().getName() + " Creating application for account..." + event.getSubject());
			
			CreateDocumentResponse createDocumentResponse = createDocument(properties.get(Properties.MONGO_CLIENT_URI), COLLECTION_NAME, application);
			
			logger.log(this.getClass().getName() + " Status Code: " + createDocumentResponse.getStatusCode());
			
			if (createDocumentResponse.getStatusCode() == 201) {
				logger.log(this.getClass().getName() + "Created Application Id: " + application.getId());
			} else {
				throw new IOException(createDocumentResponse.getErrorMessage());
			}
			
		} else if (EventAction.UPDATE.name().equals(event.getEventAction())) {
			
			UpdateDocumentResponse updateDocumentResponse = updateDocument(properties.get(Properties.MONGO_CLIENT_URI), COLLECTION_NAME, application);
			
			if (updateDocumentResponse.getStatusCode() == 200) {
				logger.log(this.getClass().getName() + " Updated Application Id: " + application.getId());
			} else {
				throw new IOException(updateDocumentResponse.getErrorMessage());
			}
			
		} else if (EventAction.DELETE.name().equals(event.getEventAction())) {
			
			DeleteDocumentResponse deleteDocumentReponse = deleteDocument(properties.get(Properties.MONGO_CLIENT_URI), COLLECTION_NAME, application);
			
			logger.log(this.getClass().getName() + " Status Code: " + deleteDocumentReponse.getStatusCode());
			
			if (deleteDocumentReponse.getStatusCode() == 204) {
				logger.log(this.getClass().getName() + " Deleted Application Id: " + application.getId());
			} else {
				throw new IOException(deleteDocumentReponse.getErrorMessage());
			}
			
		} else {
			throw new Exception( String.format("Invalid action for %s object: %s", event.getType(), event.getEventAction() ) );
		}
		
		//
		//
		//
		
		event.setTargetId(application.getId().toString());		
	}
}