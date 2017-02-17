package com.nowellpoint.client.sforce;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.nowellpoint.client.sforce.model.Identity;
import com.nowellpoint.client.sforce.model.OutboundEvent;
import com.nowellpoint.client.sforce.model.QueryResult;
import com.nowellpoint.client.sforce.model.Token;
import com.nowellpoint.client.sforce.model.sobject.DescribeSobjectResult;
import com.nowellpoint.http.HttpRequestException;
import com.nowellpoint.http.HttpResponse;
import com.nowellpoint.http.MediaType;
import com.nowellpoint.http.RestResource;
import com.nowellpoint.util.Properties;

import au.com.bytecode.opencsv.CSVWriter;

public class TestSObjectToCSV {
	
	@BeforeClass
	public static void init() {
		Properties.loadProperties(System.getenv("NOWELLPOINT_PROPERTY_STORE"));
	}
	
	@Test
	@Ignore
	public void testCreateCSV() {
		
		UsernamePasswordGrantRequest request = OauthRequests.PASSWORD_GRANT_REQUEST.builder()
				.setClientId(System.getProperty(Properties.SALESFORCE_CLIENT_ID))
				.setClientSecret(System.getProperty(Properties.SALESFORCE_CLIENT_SECRET))
				.setUsername(System.getenv("SALESFORCE_USERNAME"))
				.setPassword(System.getenv("SALESFORCE_PASSWORD"))
				.setSecurityToken(System.getenv("SALESFORCE_SECURITY_TOKEN"))
				.build();
		
		try {
			OauthAuthenticationResponse response = Authenticators.PASSWORD_GRANT_AUTHENTICATOR
					.authenticate(request);
			
			assertNotNull(response.getToken());
			
			Token token = response.getToken();
			
			Identity identity = response.getIdentity();
			
			assertNotNull(identity);
			
			long startTime = System.currentTimeMillis();
			
			OutboundEvent outboundEvent = getOutboundEvent(token.getAccessToken(), identity.getUrls().getPartner());
			
			assertNotNull(outboundEvent);
			assertNotNull(outboundEvent.getId());
			assertNotNull(outboundEvent.getObject());
			assertNotNull(outboundEvent.getObjectId());
			assertNotNull(outboundEvent.getCreatedDate());
			
			System.out.println(System.currentTimeMillis() - startTime);
			
			Client client = new Client();
			
			DescribeSobjectRequest describeSobjectRequest = new DescribeSobjectRequest()
					.withAccessToken(response.getToken().getAccessToken())
					.withSobjectsUrl(identity.getUrls().getSobjects())
					.withSobject(outboundEvent.getObject());
			
			DescribeSobjectResult describeSobjectResult = client.describeSobject(describeSobjectRequest);
			
			String queryString = "Select %s From %s Where Id = '%s'";
			queryString = String.format(queryString, describeSobjectResult
					.getFields()
					.stream()
					.map(e -> e.getName()).collect(Collectors.joining(",")), outboundEvent.getObject(), outboundEvent.getObjectId());
			
			System.out.println(queryString);
			
			System.out.println(System.currentTimeMillis() - startTime);
			
			assertNotNull(describeSobjectResult.getFields());
			
			HttpResponse httpResponse = RestResource.get(identity.getUrls().getRest().concat("queryAll"))
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
			
			saveToBucket(outboundEvent, new File("test.csv"));
			
		} catch (OauthException e) {
			System.out.println(e.getStatusCode());
			System.out.println(e.getError());
			System.out.println(e.getErrorDescription());
		} catch (HttpRequestException | IOException | ParseException e) {
			e.printStackTrace();
		}
	}
	
	private OutboundEvent getOutboundEvent(String sessionId, String partnerUrl) throws HttpRequestException, UnsupportedEncodingException, ParseException {
		
		String url = partnerUrl.substring(0, partnerUrl.lastIndexOf("/") + 1).concat("queryAll").replace("/Soap/u/", "/data/v");
		
		String queryString = "Select Event_Type__c,Id,Object__c,Object_Id__c,CreatedDate from Outbound_Event__c Where Id = ''";
		
		HttpResponse httpResponse = RestResource.get(url)
				.accept(MediaType.APPLICATION_JSON)
				.bearerAuthorization(sessionId)
    			.queryParameter("q", URLEncoder.encode(queryString,"UTF-8"))
    			.execute();
		
		QueryResult result = httpResponse.getEntity(QueryResult.class);
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
		
		OutboundEvent outboundEvent = new OutboundEvent();
		outboundEvent.setId(result.getRecords().get(0).get("Id").asText());
		outboundEvent.setEventType(result.getRecords().get(0).get("Event_Type__c").asText());
		outboundEvent.setObject(result.getRecords().get(0).get("Object__c").asText());
		outboundEvent.setObjectId(result.getRecords().get(0).get("Object_Id__c").asText());
		outboundEvent.setCreatedDate(sdf.parse(result.getRecords().get(0).get("CreatedDate").asText()));
		
		return outboundEvent;
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
	
	private void  saveToBucket(OutboundEvent outboundEvent, File file) {
		AmazonS3 s3Client = new AmazonS3Client();
		
		ObjectMetadata metadata = new ObjectMetadata();
		metadata.setContentLength(file.length());
		metadata.setSSEAlgorithm(ObjectMetadata.AES_256_SERVER_SIDE_ENCRYPTION); 
		
		String key = outboundEvent.getObject()
				.concat("/")
				.concat(outboundEvent.getObjectId())
				.concat("/")
				.concat(outboundEvent.getCreatedDate().toString());
    	
    	PutObjectRequest putObjectRequest = new PutObjectRequest("salesforce-outbound-messages", key, file).withMetadata(metadata);
    	
    	s3Client.putObject(putObjectRequest);
	}
}