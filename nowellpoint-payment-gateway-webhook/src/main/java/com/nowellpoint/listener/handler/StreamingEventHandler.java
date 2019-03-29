package com.nowellpoint.listener.handler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;

import org.bson.Document;
import org.bson.types.ObjectId;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.amazonaws.services.secretsmanager.AWSSecretsManager;
import com.amazonaws.services.secretsmanager.AWSSecretsManagerClientBuilder;
import com.amazonaws.services.secretsmanager.model.GetSecretValueRequest;
import com.amazonaws.services.secretsmanager.model.GetSecretValueResult;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;

public class StreamingEventHandler implements RequestStreamHandler {
	
	private MongoClientURI mongoClientUri = new MongoClientURI(String.format("mongodb://%s", getSecretValue("MONGO_CLIENT_URI")), new MongoClientOptions.Builder());
	private MongoClient mongoClient = new MongoClient(mongoClientUri);
	private MongoDatabase mongoDatabase = mongoClient.getDatabase(mongoClientUri.getDatabase());

	@Override
	public void handleRequest(InputStream input, OutputStream output, Context context) throws IOException {
		
		JsonNode streamingEvent = new ObjectMapper().readValue(input, JsonNode.class);
		
		ObjectId organizationId = new ObjectId(streamingEvent.get("organizationId").get("$oid").asText());
		
		Document organization = mongoDatabase.getCollection("organizations").find(Filters.eq("_id", organizationId)).first();
	
		context.getLogger().log(organization.getString("name"));
		
	}
	
	@SuppressWarnings("unchecked")
	protected String getSecretValue(String secret) {
    	AWSSecretsManager client  = AWSSecretsManagerClientBuilder.defaultClient();
    	GetSecretValueRequest getSecretValueRequest = new GetSecretValueRequest().withSecretId("/sandbox/console");
        GetSecretValueResult getSecretValueResult = client.getSecretValue(getSecretValueRequest);

        HashMap<String, String> secretMap = new HashMap<>(); 
        
        try {
        	secretMap = new ObjectMapper().readValue(getSecretValueResult.getSecretString(), HashMap.class);
    	} catch (IOException e) {
    		e.printStackTrace();
    	}
        
        return secretMap.get(secret);
    }    
}