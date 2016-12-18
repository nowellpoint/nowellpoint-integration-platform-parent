package com.nowellpoint.aws.admin;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.datamodeling.AttributeEncryptor;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.encryption.providers.DirectKmsMaterialProvider;
import com.amazonaws.services.dynamodbv2.datamodeling.encryption.providers.EncryptionMaterialsProvider;
import com.amazonaws.services.kms.AWSKMS;
import com.amazonaws.services.kms.AWSKMSClient;
import com.amazonaws.services.kms.model.AliasListEntry;
import com.amazonaws.services.kms.model.ListAliasesResult;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class PropertyServiceHandler implements RequestStreamHandler {
	
	private static AWSKMS kmsClient = new AWSKMSClient();
	private static ObjectMapper objectMapper = new ObjectMapper();
	
	private static String getKeyId(String keyAlias) {

		ListAliasesResult listAliasesResult = kmsClient.listAliases();
		
		Optional<AliasListEntry> aliasListEntry = listAliasesResult.getAliases()
				.stream()
				.filter(entry -> keyAlias.equals(keyAlias))
				.findFirst();
		
		String keyId = null;
		
		if (aliasListEntry.isPresent()) {
			keyId = aliasListEntry.get().getTargetKeyId();
		} 

		return keyId;
	}

	@Override
	public void handleRequest(InputStream inputStream, OutputStream outputStream, Context context) throws IOException {
		LambdaLogger logger = context.getLogger();
		
		JsonNode node = objectMapper.readTree(inputStream);
		
		logger.log("getting properties for: " + node.toString());
		
		String keyAlias = System.getenv("KEY_ALIAS");
		
		logger.log("using encryption key: " + keyAlias);
		
		EncryptionMaterialsProvider provider = new DirectKmsMaterialProvider(new AWSKMSClient(), getKeyId(keyAlias), null);			
		DynamoDBMapper mapper = new DynamoDBMapper(new AmazonDynamoDBClient(), DynamoDBMapperConfig.DEFAULT, new AttributeEncryptor(provider));
		
		Property property = new Property();
		property.setSubject(node.get("propertyStore").asText().toUpperCase());
		
		DynamoDBQueryExpression<Property> queryExpression = new DynamoDBQueryExpression<Property>()
				.withHashKeyValues(property);
		
		List<Property> queryResult = mapper.query(Property.class, queryExpression);
		
		Map<String,String> properties = queryResult.stream()
				.collect(Collectors.toMap(Property::getKey, p -> p.getValue()));
		
		byte[] bytes = null;
		
		try {
			bytes = objectMapper.writeValueAsBytes(properties);
		} catch (JsonProcessingException e) {
			throw new IOException(e);
		}
		
		outputStream.write(bytes);
	}
}