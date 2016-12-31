package com.nowellpoint.util;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClientBuilder;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

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
	
	public static void loadProperties(String propertyStore) {
		URIBuilder builder = new URIBuilder().setScheme("https")
				.setHost("ainsh4j3sk.execute-api.us-east-1.amazonaws.com")
				.setPath(String.format("/production/properties/%s", propertyStore));
		
		HttpClient client = HttpClientBuilder.create().build();
		try {
			HttpGet request = new HttpGet(builder.build());
			request.addHeader("x-api-key", System.getenv("X_API_KEY"));
			
			HttpResponse response = client.execute(request);
			
			if (response.getStatusLine().getStatusCode() != 200) {
				System.out.println(new ObjectMapper().readValue(response.getEntity().getContent(), JsonNode.class));
			}
			
			Map<String, String> properties = new ObjectMapper().readValue(response.getEntity().getContent(), new TypeReference<Map<String,String>>() {});
			properties.keySet().stream().forEach(key -> {
				System.setProperty(key, properties.get(key));
			});
		} catch (URISyntaxException | IOException e) {
			e.printStackTrace();
		}
	}
}