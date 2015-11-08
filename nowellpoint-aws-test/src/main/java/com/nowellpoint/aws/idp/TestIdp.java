package com.nowellpoint.aws.idp;

import java.io.IOException;

import javax.ws.rs.core.MediaType;

import static org.junit.Assert.assertNotNull;

import com.nowellpoint.aws.http.HttpResponse;
import com.nowellpoint.aws.http.RestResource;
import com.nowellpoint.aws.lambda.idp.model.ExpandedJwt;
import com.nowellpoint.aws.lambda.idp.model.Token;

public class TestIdp {

	public void main() {
		HttpResponse response;
		try {
			response = RestResource.post("https://bx3sf8ukg1.execute-api.us-east-1.amazonaws.com/v1")
					.contentType(MediaType.APPLICATION_FORM_URLENCODED)
					.accept(MediaType.APPLICATION_JSON)
					.parameter("username", System.getenv("STORMPATH_USERNAME"))
					.parameter("password", System.getenv("STORMPATH_PASSWORD"))
					.execute();
			
			Token token = response.getEntity(Token.class);
			
			assertNotNull(token);
			
			System.out.println(token.getAccessToken());
			
			response = RestResource.get("https://bx3sf8ukg1.execute-api.us-east-1.amazonaws.com/v1/token")
					.bearerAuthorization(token.getAccessToken())
					.execute();
			
			ExpandedJwt expandedJwt = response.getEntity(ExpandedJwt.class);
			
			assertNotNull(expandedJwt);
			 
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}