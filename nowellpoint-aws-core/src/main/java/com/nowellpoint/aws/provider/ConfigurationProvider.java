package com.nowellpoint.aws.provider;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.nowellpoint.aws.model.admin.Configuration;

public class ConfigurationProvider {
	
	private static Configuration configuration;
	
	private ConfigurationProvider() {

	}
	
	public static Configuration getConfiguration() {
		return getConfiguration(System.getenv("CONFIGURATION_ID"));
	}
	
	public static Configuration getConfiguration(String configurationId) {
		
		if (configurationId == null || configurationId.trim().isEmpty()) {
			throw new IllegalArgumentException("Missing Configuration Id parameter. Either set environment variable (CONFIGURATION_ID) or set parameter");
		}
		
		if (configuration == null) {;			
			DynamoDBMapper mapper = DynamoDBMapperProvider.getDynamoDBMapper();
			configuration = mapper.load(Configuration.class, configurationId);
		}
		
		return configuration;
	}
}