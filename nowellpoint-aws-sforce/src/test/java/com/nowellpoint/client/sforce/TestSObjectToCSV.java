package com.nowellpoint.client.sforce;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.FileWriter;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.BeforeClass;
import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.nowellpoint.aws.http.HttpRequestException;
import com.nowellpoint.aws.http.HttpResponse;
import com.nowellpoint.aws.http.MediaType;
import com.nowellpoint.aws.http.RestResource;
import com.nowellpoint.aws.model.admin.Properties;
import com.nowellpoint.client.sforce.model.DescribeSobjectResult;
import com.nowellpoint.client.sforce.model.Identity;
import com.nowellpoint.client.sforce.model.QueryResult;
import com.nowellpoint.client.sforce.model.Token;

import au.com.bytecode.opencsv.CSVWriter;

public class TestSObjectToCSV {
	
	@BeforeClass
	public static void init() {
		Properties.setSystemProperties(System.getenv("NCS_PROPERTY_STORE"));
	}
	
	@Test
	public void testCreateCSV() {
		
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
			
			QueryResult result = httpResponse.getEntity(QueryResult.class);
			
			assertNotNull(result.getDone());
			assertNotNull(result.getTotalSize());
			assertNotNull(result.getRecords().get(0).get("Id").asText());
			assertEquals(result.getRecords().size(), 1);
			
			convertToCsv(result.getRecords());
			
			System.out.println(System.currentTimeMillis() - startTime);
			
		} catch (OauthException e) {
			System.out.println(e.getStatusCode());
			System.out.println(e.getError());
			System.out.println(e.getErrorDescription());
		} catch (HttpRequestException | IOException e) {
			e.printStackTrace();
		}  
	}
	
	private void convertToCsv(ArrayNode records) throws IOException {
		 CSVWriter writer = new CSVWriter(new FileWriter("test.csv"));
		 
		 Map<String,String> record = parseRecord(null, records.get(0));
		 
		 Set<String> columnNames = record.keySet();
		 Collection<String> values = record.values();
		 
		 writer.writeNext(columnNames.toArray(new String[columnNames.size()]));
		 writer.writeNext(values.toArray(new String[values.size()]));
		 
		 writer.close();
	}
	
	private Map<String,String> parseRecord(String parent, JsonNode node) {
		Map<String,String> columnNames = new HashMap<String,String>();
		Iterator<Entry<String,JsonNode>> iterator = node.fields();
		while (iterator.hasNext()) {
			 Entry<String,JsonNode> entry = iterator.next();
			 if (entry.getValue().isObject()) {
				 columnNames.putAll(parseRecord(entry.getKey(), entry.getValue()));
			 } else {
				 if (parent != null) {
					 columnNames.put(parent.concat(".").concat(entry.getKey()), parseValue(entry.getValue()));
				 } else {
					 columnNames.put(entry.getKey(), parseValue(entry.getValue()));
				 }
			 }
		}
		return columnNames;
	}
	
	private String parseValue(JsonNode node) {
		return node != null ? node.asText() : "";
	}
}