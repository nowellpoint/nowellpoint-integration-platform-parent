package com.nowellpoint.aws.api;

import static org.junit.Assert.assertNotNull;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.junit.BeforeClass;
import org.junit.Test;

import com.nowellpoint.aws.http.HttpRequestException;
import com.nowellpoint.aws.http.HttpResponse;
import com.nowellpoint.aws.http.MediaType;
import com.nowellpoint.aws.http.RestResource;
import com.nowellpoint.aws.model.admin.Properties;
import com.nowellpoint.client.sforce.Authenticators;
import com.nowellpoint.client.sforce.Client;
import com.nowellpoint.client.sforce.GetIdentityRequest;
import com.nowellpoint.client.sforce.OauthAuthenticationResponse;
import com.nowellpoint.client.sforce.OauthException;
import com.nowellpoint.client.sforce.OauthRequests;
import com.nowellpoint.client.sforce.UsernamePasswordGrantRequest;
import com.nowellpoint.client.sforce.model.Identity;
import com.nowellpoint.client.sforce.model.Token;
import com.sforce.soap.partner.Connector;
import com.sforce.soap.partner.PartnerConnection;
import com.sforce.soap.partner.QueryResult;
import com.sforce.soap.partner.sobject.SObject;
import com.sforce.ws.ConnectionException;
import com.sforce.ws.ConnectorConfig;

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
			
			GetIdentityRequest getIdentityRequest = new GetIdentityRequest()
					.setAccessToken(response.getToken().getAccessToken())
					.setId(response.getToken().getId());
			
			Client client = new Client();
			
			Identity identity = client.getIdentity(getIdentityRequest);
			
			assertNotNull(identity);
			
			HttpResponse httpResponse = RestResource.get(identity.getUrls().getQuery())
					.accept(MediaType.APPLICATION_JSON)
					.bearerAuthorization(token.getAccessToken())
	    			.queryParameter("q", URLEncoder.encode("Select Id, Name from Account","UTF-8"))
	    			.execute();
			
			System.out.println(httpResponse.getAsString());
			
		} catch (OauthException e) {
			System.out.println(e.getStatusCode());
			System.out.println(e.getError());
			System.out.println(e.getErrorDescription());
		} catch (HttpRequestException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		
		ConnectorConfig config = new ConnectorConfig();
		config.setAuthEndpoint(String.format("%s/services/Soap/u/36.0", "https://login.salesforce.com"));
		config.setUsername(System.getenv("SALESFORCE_USERNAME"));
		config.setPassword(System.getenv("SALESFORCE_PASSWORD").concat(System.getenv("SALESFORCE_SECURITY_TOKEN")));
		
		QueryResult queryResult = null;
		
		try {
			PartnerConnection connection = Connector.newConnection(config);
			queryResult = connection.query("Select Id, Name from Account");
		} catch (ConnectionException e) {
			e.printStackTrace();
		}	
		
		
		//try {
			
			SObject[] sobjects = queryResult.getRecords();
			
			System.out.println(sobjects[0].getField("Id"));
			
			//JAXBContext context = JAXBContext.newInstance( Record.class );
			//Unmarshaller unmarshaller = context.createUnmarshaller();
			//unmarshaller.unmarshal(sobjects[0].getField("Id"), Record.class );
		//} catch (JAXBException e) {
			// TODO Auto-generated catch block
		//	e.printStackTrace();
		//}
	}
}