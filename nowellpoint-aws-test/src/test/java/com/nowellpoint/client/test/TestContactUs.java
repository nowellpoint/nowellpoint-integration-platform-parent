package com.nowellpoint.client.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.nowellpoint.http.HttpResponse;
import com.nowellpoint.http.MediaType;
import com.nowellpoint.http.RestResource;
import com.nowellpoint.http.Status;

public class TestContactUs {
	
	@Test
	public void testContactUs() {
		HttpResponse httpResponse = RestResource.post(System.getenv("NOWELLPOINT_API_ENDPOINT"))
    			.contentType(MediaType.APPLICATION_FORM_URLENCODED)
    			.path("contact")
				.parameter("leadSource", "Contact")
				.parameter("firstName", "John")
				.parameter("lastName", "Herson")
				.parameter("email", "test.nowellpoint@mailinator.com")
				.parameter("phone", "919-000-0100")
				.parameter("company", "Nowellpoint")
				.parameter("description", "Testing the contact us form")
    			.execute();
		
		assertEquals(Status.OK.intValue(), httpResponse.getStatusCode());
		
		System.out.println(httpResponse.getStatusCode());
		System.out.println(httpResponse.getAsString());
	}
}