package com.nowellpoint.api.service.test;

import org.junit.Test;

import com.nowellpoint.aws.http.HttpResponse;
import com.nowellpoint.aws.http.MediaType;
import com.nowellpoint.aws.http.RestResource;
import com.nowellpoint.aws.model.admin.Properties;

public class TestPropertyService {
	
	@Test
	public void testGetProperties() {
		HttpResponse httpResponse = RestResource.get("https://ainsh4j3sk.execute-api.us-east-1.amazonaws.com")
				.path("production")
				.path("properties")
				.path("sandbox")
				.header("x-api-key", System.getenv("X_API_KEY"))
				.execute();
		
		System.out.println(httpResponse.getStatusCode());
		System.out.println(httpResponse.getAsString());
	}

}
