package com.nowellpoint.aws.admin;

import org.joda.time.Instant;

import com.amazonaws.util.json.JSONException;
import com.amazonaws.util.json.JSONObject;
import com.nowellpoint.aws.model.admin.Configuration;
import com.nowellpoint.aws.provider.DynamoDBMapperProvider;

public class CreateConfiguration {

	public CreateConfiguration() {
		
		JSONObject node = null;
		try {
			node = new JSONObject().put("salesforce_username", System.getenv("SALESFORCE_USERNAME"))
					.put("salesforce_password", System.getenv("SALESFORCE_PASSWORD"))
					.put("salesforce_security_token", System.getenv("SALESFORCE_SECURITY_TOKEN"))
					.put("salesforce_client_id", System.getenv("SALESFORCE_CLIENT_ID"))
					.put("salesforce_client_secret", System.getenv("SALESFORCE_CLIENT_SECRET"))
					.put("salesforce_refresh_uri", System.getenv("SALESFORCE_REFRESH_URI"))
					.put("salesforce_revoke_uri", System.getenv("SALESFORCE_REVOKE_URI"))
					.put("salesforce_token_uri", System.getenv("SALESFORCE_TOKEN_URI"))
					.put("stormpath_api_key_id", System.getenv("STORMPATH_API_KEY_ID"))
					.put("stormpath_api_key_secret", System.getenv("STORMPATH_API_KEY_SECRET"))
					.put("stormpath_api_endpoint", System.getenv("STORMPATH_API_ENDPOINT"))
					.put("stormpath_application_id", System.getenv("STORMPATH_APPLICATION_ID"))
					.put("stormpath_directory_id", System.getenv("STORMPATH_DIRECTORY_ID"))
					.put("loggly_api_key", System.getenv("LOGGLY_API_KEY"))
					.put("redirect_uri", System.getenv("REDIRECT_URI"))
					.put("aws_kms_key_id", System.getenv("AWS_KMS_KEY_ID"))
					.put("sendgrid_api_key", System.getenv("SENDGRID_API_KEY"))
					.put("default_organization_id", System.getenv("DEFAULT_ORGANIZATION_ID"))
					.put("default_user_id", System.getenv("DEFAULT_USER_ID"))
					.put("mongo_client_uri", System.getenv("MONGO_CLIENT_URI"));
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		System.out.println(node.toString());
		
		Configuration configuration = DynamoDBMapperProvider.getDynamoDBMapper().load(Configuration.class, "4877db51-fccf-4e8e-b012-6ba76d4d76f7");
		configuration.withLastModifiedDate(Instant.now().toDate()).withConfigurationFile(node.toString());
				
		/**
		 * 
		 */
		
		DynamoDBMapperProvider.getDynamoDBMapper().save(configuration);
	}

	public static void main(String[] args) {
		new CreateConfiguration();
	}
}
