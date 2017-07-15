package com.nowellpoint.util;

import java.util.AbstractMap;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.lambda.AWSLambda;
import com.amazonaws.services.lambda.AWSLambdaClientBuilder;
import com.amazonaws.services.lambda.invoke.LambdaInvokerFactory;
import com.nowellpoint.service.PropertyService;

public class Properties {
	
	public static final String LOGGLY_API_KEY = "loggly.api.key";
	public static final String LOGGLY_API_ENDPOINT = "loggly.api.endpoint";
	public static final String MONGO_CLIENT_URI = "mongo.client.uri";
	public static final String SALESFORCE_CLIENT_ID = "salesforce.client.id";
	public static final String SALESFORCE_CLIENT_SECRET = "salesforce.client.secret";
	public static final String SALESFORCE_AUTHORIZE_URI = "salesforce.authorize.uri";
	public static final String SALESFORCE_TOKEN_URI = "salesforce.token.uri";
	public static final String SALESFORCE_REFRESH_URI = "salesforce.refresh.uri";
	public static final String SALESFORCE_REVOKE_URI = "salesforce.revoke.uri";
	public static final String SALESFORCE_USERNAME = "salesforce.username";
	public static final String SALESFORCE_PASSWORD = "salesforce.password";
	public static final String SALESFORCE_SECURITY_TOKEN = "salesforce.security.token";
	public static final String SALESFORCE_REDIRECT_URI = "salesforce.redirect.uri";
	public static final String SALESFORCE_API_VERSION = "salesforce.api.version";
	public static final String STORMPATH_API_KEY_ID = "stormpath.api.key.id";
	public static final String STORMPATH_API_KEY_SECRET = "stormpath.api.key.secret";
	public static final String STORMPATH_API_ENDPOINT = "stormpath.api.endpoint";
	public static final String STORMPATH_APPLICATION_ID = "stormpath.application.id";
	public static final String STORMPATH_DIRECTORY_ID = "stormpath.directory.id";
	public static final String STORMPATH_GROUP_ID = "stormpath.group.id";
	public static final String OKTA_API_KEY = "okta.api.key";
	public static final String OKTA_ORG_URL = "okta.org.url";
	public static final String SENDGRID_API_KEY = "sendgrid.api.key";
	public static final String REDIS_PASSWORD = "redis.password";
	public static final String REDIS_HOST = "redis.host";
	public static final String REDIS_PORT = "redis.port";
	public static final String DEFAULT_SUBJECT = "default.subject";
	public static final String CACHE_DATA_ENCRYPTION_KEY = "cache.data.encryption.key";
	public static final String BRAINTREE_ENVIRONMENT = "braintree.environment";
	public static final String BRAINTREE_MERCHANT_ID = "braintree.merchant.id";
	public static final String BRAINTREE_PUBLIC_KEY = "braintree.public.key";
	public static final String BRAINTREE_PRIVATE_KEY = "braintree.private.key";
	public static final String VERIFY_EMAIL_REDIRECT = "verify.email.redirect";
	public static final String CLOUDFRONT_HOSTNAME = "cloudfront.hostname";
	public static final String APPLICATION_HOSTNAME = "application.hostname";
	
	public static void loadProperties(String propertyStore) {
		Map<String,String> properties = getProperties(propertyStore);
		properties.keySet().stream().forEach(key -> {
			System.setProperty(key, properties.get(key));
		});
	}
	
	public static Map<String,String> getProperties(String propertyStore) {
		AWSLambda lambdaClient = AWSLambdaClientBuilder
				.standard()
				.withRegion(Regions.US_EAST_1)
				.build();
		
		Map<String,String> input = buildPropertyStore(propertyStore);
		
		PropertyService propertyService = LambdaInvokerFactory.builder()
				.lambdaClient(lambdaClient)
				.build(PropertyService.class);
		
		return propertyService.getProperties(input);
	}
	
	public static String getProperty(String key) {
		return System.getProperty(key);
	}
	
	private static Map<String,String> buildPropertyStore(String propertyStore) {
		return Collections.unmodifiableMap(Stream.of(new AbstractMap.SimpleEntry<>("propertyStore", propertyStore))
				.collect(Collectors.toMap((e) -> e.getKey(), (e) -> e.getValue())));
	}
}