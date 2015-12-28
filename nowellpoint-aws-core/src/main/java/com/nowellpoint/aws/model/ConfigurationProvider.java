package com.nowellpoint.aws.model;

import com.nowellpoint.aws.model.admin.Configuration;

public class ConfigurationProvider {
	
	private static Configuration configuration;
	
	public static Configuration getConfiguration() {
		if (configuration == null) {
			configuration = DynamoDBMapperProvider.getDynamoDBMapper().load(Configuration.class, "4877db51-fccf-4e8e-b012-6ba76d4d76f7");
		}
		return configuration;
	}
}