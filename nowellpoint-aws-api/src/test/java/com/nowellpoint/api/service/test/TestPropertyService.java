package com.nowellpoint.api.service.test;

import java.io.IOException;
import java.util.Map;

import org.junit.Test;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nowellpoint.aws.http.HttpRequestException;
import com.nowellpoint.aws.http.HttpResponse;
import com.nowellpoint.aws.http.RestResource;

public class TestPropertyService {
	
	@Test
	public void testGetProperties() {
		
		HttpResponse httpResponse = RestResource.get("https://ainsh4j3sk.execute-api.us-east-1.amazonaws.com")
				.path("production")
				.path("properties")
				.path("sandbox")
				.header("x-api-key", System.getenv("X_API_KEY"))
				.execute();
		
		Map<String, String> properties;
		try {
			properties = new ObjectMapper().readValue(httpResponse.getAsString(), new TypeReference<Map<String,String>>() {});
			properties.keySet().stream().forEach(key -> {
				System.setProperty(key, properties.get(key));
			});
		} catch (HttpRequestException | IOException e) {
			e.printStackTrace();
		}
	}
}