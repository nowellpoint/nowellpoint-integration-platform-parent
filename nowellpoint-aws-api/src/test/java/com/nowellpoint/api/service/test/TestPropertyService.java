package com.nowellpoint.api.service.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;

//import org.apache.http.HttpResponse;
//import org.apache.http.client.HttpClient;
//import org.apache.http.client.methods.HttpGet;
//import org.apache.http.client.utils.URIBuilder;
//import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.Test;

import com.nowellpoint.aws.http.RestResource;
import com.nowellpoint.aws.http.HttpResponse;
import com.nowellpoint.aws.http.MediaType;
import com.nowellpoint.aws.http.RestResource;
import com.nowellpoint.aws.model.admin.Properties;

public class TestPropertyService {
	
	@Test
	public void testGetProperties() {
		
//		URIBuilder builder = new URIBuilder().setScheme("https")
//				.setHost("ainsh4j3sk.execute-api.us-east-1.amazonaws.com")
//				.setPath("/production/properties/sandbox");
//		
//		HttpClient client = HttpClientBuilder.create().build();
//		HttpGet request;
//		try {
//			request = new HttpGet(builder.build());
//			request.addHeader("x-api-key", System.getenv("X_API_KEY"));
//			HttpResponse response = client.execute(request);
//			BufferedReader rd = new BufferedReader(
//					new InputStreamReader(response.getEntity().getContent()));
//
//				StringBuffer result = new StringBuffer();
//				String line = "";
//				while ((line = rd.readLine()) != null) {
//					result.append(line);
//				}
//				System.out.println(result);
//		} catch (URISyntaxException | IOException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
		
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
