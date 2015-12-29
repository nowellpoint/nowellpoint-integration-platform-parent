package com.nowellpoint.aws.event;

import java.io.IOException;
import java.util.logging.Logger;

import org.bson.types.ObjectId;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.nowellpoint.aws.client.DataClient;
import com.nowellpoint.aws.model.Configuration;
import com.nowellpoint.aws.model.data.CreateDocumentRequest;
import com.nowellpoint.aws.model.data.CreateDocumentResponse;

public class UserEventHandler implements AbstractEventHandler {
	
	private static final Logger log = Logger.getLogger(UserEventHandler.class.getName());

	@Override
	public String process(String payload) throws IOException {
		
		log.info("starting UserEventHandler");
		
		String userId = new ObjectId().toString();
		
		ObjectNode node = objectMapper.readValue(payload, ObjectNode.class);
		node.put("_id", userId);
		
		CreateDocumentRequest createDocumentRequest = new CreateDocumentRequest().withMongoDBConnectUri(Configuration.getMongoClientUri())
				.withUserId(userId)
				.withCollectionName("users")
				.withDocument(node.toString());
		
		final DataClient dataClient = new DataClient();
			
		CreateDocumentResponse createDocumentResponse = dataClient.create(createDocumentRequest);	
		
		log.info("Status Code: " + createDocumentResponse.getStatusCode());
		log.info(createDocumentResponse.getId());
		log.info(createDocumentResponse.getErrorMessage());
		
		//
		//
		//
		
		return createDocumentResponse.getId();
		
	}
}