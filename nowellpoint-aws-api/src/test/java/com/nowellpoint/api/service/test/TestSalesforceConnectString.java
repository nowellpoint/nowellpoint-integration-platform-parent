package com.nowellpoint.api.service.test;

import static com.sforce.soap.partner.Connector.newConnection;
import static org.junit.Assert.assertEquals;

import java.util.Base64;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.junit.Test;

import com.nowellpoint.api.rest.domain.ConnectionString;
import com.nowellpoint.api.rest.domain.SalesforceConnectionString;
import com.nowellpoint.util.Properties;
import com.sforce.soap.partner.PartnerConnection;
import com.sforce.soap.partner.fault.LoginFault;
import com.sforce.ws.ConnectionException;
import com.sforce.ws.ConnectorConfig;

public class TestSalesforceConnectString {
	
	@Test
	public void testLoginAuthentication() {
		String authEndpoint = "https://login.salesforce.com";
		
		ConnectorConfig config = new ConnectorConfig();
		config.setAuthEndpoint(String.format("%s/services/Soap/u/%s", authEndpoint, System.getProperty(Properties.SALESFORCE_API_VERSION)));
		config.setUsername(System.getenv("SALESFORCE_USERNAME"));
		config.setPassword(System.getenv("SALESFORCE_PASSWORD").concat(System.getenv("SALESFORCE_SECURITY_TOKEN")));
		
		try {
			PartnerConnection connection = newConnection(config);

			String accessToken = connection.getConfig().getSessionId();
			String id = String.format("%s/id/%s/%s", authEndpoint, connection.getUserInfo().getOrganizationId(), connection.getUserInfo().getUserId());
			String instanceUrl = connection.getConfig().getServiceEndpoint().substring(0, connection.getConfig().getServiceEndpoint().indexOf("/services"));
			Long issuedAt = connection.getServerTimestamp().getTimestamp().getTimeInMillis();
			String tokenType = "Bearer";
			String signature = id.concat(String.valueOf(issuedAt));
			
			String secret = System.getenv("STORMPATH_API_KEY_SECRET");

		    try {
		        Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
		        SecretKeySpec secret_key = new SecretKeySpec(secret.getBytes(), "HmacSHA256");
		        sha256_HMAC.init(secret_key);
		        signature = Base64.getEncoder().encodeToString(sha256_HMAC.doFinal(signature.getBytes()));
		    } catch (Exception e) {

		    }
						
			System.out.println("access token: " + accessToken);
			System.out.println("id: " + id);
			System.out.println("instanceUrl: " + instanceUrl);
			System.out.println("issued at: " + issuedAt);
			System.out.println("token type: " + tokenType);
			System.out.println("signature: " + signature);
			
		} catch (ConnectionException e) {
			if (e instanceof LoginFault) {
				LoginFault loginFault = (LoginFault) e;
				System.out.println(loginFault.getExceptionCode().name().concat(": ").concat(loginFault.getExceptionMessage()));
			} else {
				e.printStackTrace();
			}
		}
	}
		
	@Test
	public void testSalesforceConnectString() {
		ConnectionString connectionString = ConnectionString.salesforce(
				"eb8e22d95ede4f7780309959ae83d957", 
				"https://login.salesforce.com/id/00D300000000lnEEAQ/00530000000fo9KAAQ", 
				SalesforceConnectionString.REFRESH_TOKEN);
		
		SalesforceConnectionString salesforce = SalesforceConnectionString.of(connectionString);
		
		assertEquals(salesforce.getHostname(), "https://login.salesforce.com");
		assertEquals(salesforce.getCredentials(), "eb8e22d95ede4f7780309959ae83d957");
		assertEquals(salesforce.getGrantType(), "refresh_token");
		assertEquals(salesforce.getOrganizationId(), "00D300000000lnEEAQ");
		assertEquals(salesforce.getUserId(), "00530000000fo9KAAQ");
	}
}