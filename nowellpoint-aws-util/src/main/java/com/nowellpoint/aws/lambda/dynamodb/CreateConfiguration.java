package com.nowellpoint.aws.lambda.dynamodb;

import org.joda.time.Instant;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.nowellpoint.aws.model.DynamoDBMapperProvider;
import com.nowellpoint.aws.model.config.Configuration;

public class CreateConfiguration {
	
	public void handle() {
		//
		//
		//
		
		String payload = null;
		try {
			payload = objectMapper.writeValueAsString(resource);
		} catch (JsonProcessingException e) {
			throw new WebApplicationException(e);
		}
		
		//
		//
		//
		
		Configuration configuration = new Configuration().withCreatedDate(Instant.now().toDate())
				.withLastModifiedDate(Instant.now().toDate())
				.withPayload(payload);
		
		//
		//
		//
		
		DynamoDBMapperProvider.getDynamoDBMapper().save(configuration);
	}

}
