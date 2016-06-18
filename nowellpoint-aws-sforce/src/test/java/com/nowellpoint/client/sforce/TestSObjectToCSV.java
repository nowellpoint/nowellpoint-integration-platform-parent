package com.nowellpoint.client.sforce;


import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.stream.Collectors;

import org.junit.BeforeClass;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.nowellpoint.aws.http.HttpRequestException;
import com.nowellpoint.aws.http.HttpResponse;
import com.nowellpoint.aws.http.MediaType;
import com.nowellpoint.aws.http.RestResource;
import com.nowellpoint.aws.model.admin.Properties;
import com.nowellpoint.client.sforce.Authenticators;
import com.nowellpoint.client.sforce.Client;
import com.nowellpoint.client.sforce.DescribeGlobalSobjectsRequest;
import com.nowellpoint.client.sforce.DescribeSobjectRequest;
import com.nowellpoint.client.sforce.GetIdentityRequest;
import com.nowellpoint.client.sforce.OauthAuthenticationResponse;
import com.nowellpoint.client.sforce.OauthException;
import com.nowellpoint.client.sforce.OauthRequests;
import com.nowellpoint.client.sforce.UsernamePasswordGrantRequest;
import com.nowellpoint.client.sforce.model.DescribeGlobalSobjectsResult;
import com.nowellpoint.client.sforce.model.DescribeSobjectResult;
import com.nowellpoint.client.sforce.model.Identity;
import com.nowellpoint.client.sforce.model.Token;

public class TestSObjectToCSV {
	
	@BeforeClass
	public static void init() {
		Properties.setSystemProperties(System.getenv("NCS_PROPERTY_STORE"));
	}
	
	@Test
	public void testXmlQuery() {
		
		UsernamePasswordGrantRequest request = OauthRequests.USERNAME_PASSWORD_GRANT_REQUEST.builder()
				.setClientId(System.getProperty(Properties.SALESFORCE_CLIENT_ID))
				.setClientSecret(System.getProperty(Properties.SALESFORCE_CLIENT_SECRET))
				.setUsername(System.getenv("SALESFORCE_USERNAME"))
				.setPassword(System.getenv("SALESFORCE_PASSWORD"))
				.setSecurityToken(System.getenv("SALESFORCE_SECURITY_TOKEN"))
				.build();
		
		try {
			OauthAuthenticationResponse response = Authenticators.USERNAME_PASSWORD_GRANT_AUTHENTICATOR
					.authenticate(request);
			
			assertNotNull(response.getToken());
			
			Token token = response.getToken();
			
			Client client = new Client();
			
			GetIdentityRequest getIdentityRequest = new GetIdentityRequest()
					.setAccessToken(response.getToken().getAccessToken())
					.setId(response.getToken().getId());
			
			Identity identity = client.getIdentity(getIdentityRequest);
			
			assertNotNull(identity);
			
//			DescribeGlobalSobjectsRequest describeSobjectsRequest = new DescribeGlobalSobjectsRequest()
//					.setAccessToken(response.getToken().getAccessToken())
//					.setSobjectsUrl(identity.getUrls().getSobjects());
//			
//			DescribeGlobalSobjectsResult result = client.describeGlobal(describeSobjectsRequest);
			
			long startTime = System.currentTimeMillis();
			
			DescribeSobjectRequest describeSobjectRequest = new DescribeSobjectRequest()
					.setAccessToken(response.getToken().getAccessToken())
					.setSobjectsUrl(identity.getUrls().getSobjects())
					.setSobject("Account");
			
			DescribeSobjectResult describeSobjectResult = client.describeSobject(describeSobjectRequest);
			
			String queryString = "Select %s From Account Limit 1";
			queryString = String.format(queryString, describeSobjectResult
					.getFields()
					.stream()
					.map(e -> e.getName()).collect(Collectors.joining(",")));
			
			System.out.println(System.currentTimeMillis() - startTime);
			
			assertNotNull(describeSobjectResult.getFields());
			
			HttpResponse httpResponse = RestResource.get(identity.getUrls().getQuery())
					.accept(MediaType.APPLICATION_JSON)
					.bearerAuthorization(token.getAccessToken())
	    			.queryParameter("q", URLEncoder.encode(queryString,"UTF-8"))
	    			.execute();
			
			System.out.println(System.currentTimeMillis() - startTime);
			
			parseJson(httpResponse.getAsString());
			
		} catch (OauthException e) {
			System.out.println(e.getStatusCode());
			System.out.println(e.getError());
			System.out.println(e.getErrorDescription());
		} catch (HttpRequestException | UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void parseJson(String jsonString) throws JsonParseException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		JsonFactory jsonFactory = new JsonFactory(); 
		JsonParser parser = jsonFactory.createParser(jsonString);
		JsonToken token = parser.nextToken();

		while (true) {

		    if (!JsonToken.START_OBJECT.equals(token)) {
		        break;
		    }
		    if (token == null) {
		        break;
		    }

		    ObjectNode node = mapper.readTree(parser);
		    
		    System.out.println("totalSize: " + node.get("totalSize").asInt());

		}
	}
}