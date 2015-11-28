package com.nowellpoint.aws.tools;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import com.amazonaws.util.json.JSONException;
import com.amazonaws.util.json.JSONObject;

public class ConfigurationFileGenerator {

	public static void main(String[] args) {
		System.out.println("generating configuration.json");
		JSONObject payload = null;
		try {
			payload = new JSONObject().put("salesforce_client_id", System.getenv("SALESFORCE_CLIENT_ID"))
					.put("salesforce_client_secret", System.getenv("SALESFORCE_CLIENT_SECRET"))
					.put("loggly_api_key", System.getenv("LOGGLY_API_KEY"))
					.put("aws_kms_key_id", System.getenv("AWS_KMS_KEY_ID"))
					.put("stormpath_api_key_id", System.getenv("STORMPATH_API_KEY_ID"))
					.put("stormpath_api_key_secret", System.getenv("STORMPATH_API_KEY_SECRET"))
					.put("stormpath_application_id", System.getenv("STORMPATH_APPLICATION_ID"))
					.put("stormpath_api_endpoint", System.getenv("STORMPATH_API_ENDPOINT"))
					.put("stormpath_directory_id", System.getenv("STORMPATH_DIRECTORY_ID"))
					.put("stormpath_directory", System.getenv("STORMPATH_DIRECTORY"))
					.put("redirect_uri", System.getenv("REDIRECT_URI"))
					.put("mongo_client_uri", System.getenv("MONGO_CLIENT_URI"))
					.put("salesforce_token_uri", System.getenv("SALESFORCE_TOKEN_URI"))
					.put("salesforce_refresh_uri", System.getenv("SALESFORCE_REFRESH_URI"))
					.put("salesforce_revoke_uri", System.getenv("SALESFORCE_REVOKE_URI"));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		FileWriter fileWriter = null;
		try {
			File file = new File("/Users/jherson/Dropbox/config/configuration.json");
			fileWriter = new FileWriter(file);
			fileWriter.write(payload.toString());
			fileWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (fileWriter != null) {
				try {
					fileWriter.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}