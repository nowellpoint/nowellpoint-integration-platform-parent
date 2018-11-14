package com.nowellpoint.console.util;

import java.io.IOException;
import java.util.HashMap;

import com.amazonaws.services.secretsmanager.AWSSecretsManager;
import com.amazonaws.services.secretsmanager.AWSSecretsManagerClientBuilder;
import com.amazonaws.services.secretsmanager.model.GetSecretValueRequest;
import com.amazonaws.services.secretsmanager.model.GetSecretValueResult;
import com.fasterxml.jackson.databind.ObjectMapper;

public class SecretsManager {
	
	public String getSecret(String secretName) {
		AWSSecretsManager client = AWSSecretsManagerClientBuilder.defaultClient();

	    GetSecretValueRequest getSecretValueRequest = new GetSecretValueRequest().withSecretId("sandbox/console");

	    GetSecretValueResult getSecretValueResult = client.getSecretValue(getSecretValueRequest);
	    
	    final ObjectMapper objectMapper = new ObjectMapper();

	    try {
			@SuppressWarnings("unchecked")
			final HashMap<String, String> secretMap = objectMapper.readValue(getSecretValueResult.getSecretString(), HashMap.class);
			return secretMap.get(secretName);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

}
