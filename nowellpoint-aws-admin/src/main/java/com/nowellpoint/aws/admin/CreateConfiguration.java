package com.nowellpoint.aws.admin;

import org.joda.time.Instant;

import com.amazonaws.util.json.JSONException;
import com.amazonaws.util.json.JSONObject;
import com.nowellpoint.aws.model.DynamoDBMapperProvider;
import com.nowellpoint.aws.model.admin.Configuration;

public class CreateConfiguration {

	public CreateConfiguration() {
		
		JSONObject node = null;
		try {
			node = new JSONObject().put("salesforce", new JSONObject().put("leadOwnerId", "00G30000002tkYtEAI")
					.put("username", System.getenv("SALESFORCE_USERNAME"))
					.put("password", System.getenv("SALESFORCE_PASSWORD"))
					.put("securityToken", System.getenv("SALESFORCE_SECURITY_TOKEN")));
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		System.out.println(node.toString());
		
		Configuration configuration = new Configuration().withId("4877db51-fccf-4e8e-b012-6ba76d4d76f7")
				//.withCreatedDate(Instant.now().toDate())
				.withLastModifiedDate(Instant.now().toDate())
				.withPayload(node.toString());
		/**
		 * 
		 */
		
		DynamoDBMapperProvider.getDynamoDBMapper().save(configuration);
	}

	public static void main(String[] args) {
		new CreateConfiguration();
	}
}
