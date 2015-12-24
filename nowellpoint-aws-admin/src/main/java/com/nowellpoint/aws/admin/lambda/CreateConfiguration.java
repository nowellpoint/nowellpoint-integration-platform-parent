package com.nowellpoint.aws.admin.lambda;

import org.joda.time.Instant;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.nowellpoint.aws.model.DynamoDBMapperProvider;
import com.nowellpoint.aws.model.admin.Configuration;
import com.nowellpoint.aws.model.admin.CreateConfigurationRequest;
import com.nowellpoint.aws.model.admin.CreateConfigurationResponse;

public class CreateConfiguration implements RequestHandler<CreateConfigurationRequest, CreateConfigurationResponse> {
	
	public CreateConfigurationResponse handleRequest(CreateConfigurationRequest request, Context context) {
		
		/**
		 * 
		 */
		
		CreateConfigurationResponse response = new CreateConfigurationResponse();
		
		/**
		 * 
		 */
		
		Configuration configuration = new Configuration().withCreatedDate(Instant.now().toDate())
				.withLastModifiedDate(Instant.now().toDate())
				.withPayload(request.getPayload());
		/**
		 * 
		 */
		
		DynamoDBMapperProvider.getDynamoDBMapper().save(configuration);
		
		/**
		 * 
		 */
		
		response.setId(configuration.getId());
		
		/**
		 * 
		 */
		
		return response;
	}
}