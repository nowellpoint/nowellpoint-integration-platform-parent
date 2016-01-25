package com.nowellpoint.aws.event;

import java.io.IOException;
import java.util.Map;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.nowellpoint.aws.model.Event;
import com.nowellpoint.aws.model.EventAction;
import com.nowellpoint.aws.model.admin.Properties;
import com.nowellpoint.aws.model.data.CreateDocumentResponse;
import com.nowellpoint.aws.model.data.Project;
import com.nowellpoint.aws.model.data.UpdateDocumentResponse;

public class ProjectEventHandler extends AbstractDocumentEventHandler {
	
	private static final String COLLECTION_NAME = "projects";
	
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
		
		logger.log(this.getClass().getName() + " starting ProjectEventHandler");
		
		//
		//
		//
		
		Project project = objectMapper.readValue(event.getPayload(), Project.class);
		
		//
		//
		//
		
		if (EventAction.CREATE.name() == event.getEventAction()) {
			
			project.setId(event.getId());
			project.setCreatedById(event.getSubjectId());
			project.setLastModifiedById(event.getSubjectId());
			
			logger.log(this.getClass().getName() + " Creating project for account..." + event.getSubjectId());
				
			CreateDocumentResponse createDocumentResponse = createDocument(properties.get(Properties.MONGO_CLIENT_URI), COLLECTION_NAME, project);
			
			logger.log(this.getClass().getName() + " Status Code: " + createDocumentResponse.getStatusCode());
			
			if (createDocumentResponse.getStatusCode() == 201) {
				logger.log(this.getClass().getName() + " Document Id: " + createDocumentResponse.getId());
			} else {
				throw new IOException(createDocumentResponse.getErrorMessage());
			}
			
		} else if (EventAction.UPDATE.name() == event.getEventAction()) {
			
			project.setLastModifiedById(event.getSubjectId());
			
			UpdateDocumentResponse updateDocument = updateDocument(properties.get(Properties.MONGO_CLIENT_URI), COLLECTION_NAME, project);
			
			if (updateDocument.getStatusCode() == 200) {
				logger.log(this.getClass().getName() + " Document Id: " + updateDocument.getId());
			} else {
				throw new IOException(updateDocument.getErrorMessage());
			}
		}
		
		//
		//
		//
		
		addDocumentToCache(project, properties);
		
		//
		//
		//
		
		event.setTargetId(project.getId());		
	}
}