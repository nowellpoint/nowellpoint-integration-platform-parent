package com.nowellpoint.sforce;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;

import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClientBuilder;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.events.DynamodbEvent;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.nowellpoint.sforce.model.Notification;
import com.nowellpoint.sforce.model.OutboundMessage;
import com.nowellpoint.sforce.model.Sobject;

public class OutboundMessageHandler {
	
	private static DynamoDBMapper mapper = new DynamoDBMapper(new AmazonDynamoDBClient());
	
	private long startTime = System.currentTimeMillis();

	public String handleEvent(DynamodbEvent event, Context context) {
		
		LambdaLogger logger = context.getLogger();
		
		event.getRecords().stream().filter(record -> "INSERT".equals(record.getEventName())).forEach(record -> {
			
			logger.log("DynamodbEvent received...Event Id: "
					.concat(record.getEventID())
					.concat(" Event Name: " + record.getEventName()));
			
			String organizationId = record.getDynamodb().getKeys().get("OrganizationId").getS();
			String key = record.getDynamodb().getKeys().get("Key").getS();
			
			System.out.println("ready to process in ms4: " + String.valueOf(System.currentTimeMillis() - startTime));
			
			OutboundMessage outboundMessage = mapper.load(OutboundMessage.class, organizationId, key);
			
			System.out.println("ready to process in ms: " + String.valueOf(System.currentTimeMillis() - startTime));
			
			try {
				process(outboundMessage);
				outboundMessage.setStatus("PROCESSED");
				startTime = System.currentTimeMillis();
			} catch (Exception e) {
				outboundMessage.setStatus("FAILED");
				outboundMessage.setErrorMessage(e.getMessage());
			} finally {
				outboundMessage.setProcessedDate(Date.from(Instant.now()));
				outboundMessage.setDuration(System.currentTimeMillis() - outboundMessage.getReceivedDate().getTime());
				outboundMessage.setSessionId(null);
				mapper.save(outboundMessage);
				System.out.println("write back to db in ms: " + String.valueOf(System.currentTimeMillis() - startTime));
			}
		});
		
		return "ok";
	}
	
	private void process(OutboundMessage outboundMessage) throws ClientProtocolException, IOException, URISyntaxException {
		
		long startTime = System.currentTimeMillis();
		
		HttpClient client = HttpClientBuilder.create().build();
		
		String url = outboundMessage
				.getPartnerUrl()
				.substring(0, outboundMessage
						.getPartnerUrl()
						.lastIndexOf("/") + 1)
				.replace("/Soap/u/", "/data/v")
				.concat("queryAll");
		
		URIBuilder builder = new URIBuilder(url);
		
		String queryString = "Id,IsDeleted,AccountId,IsPrivate,Name,Description,StageName,Amount,Probability,ExpectedRevenue,TotalOpportunityQuantity,CloseDate,Type,NextStep,LeadSource,IsClosed,IsWon,ForecastCategory,ForecastCategoryName,CampaignId,HasOpportunityLineItem,Pricebook2Id,OwnerId,CreatedDate,CreatedById,LastModifiedDate,LastModifiedById,SystemModstamp,LastActivityDate,FiscalQuarter,FiscalYear,Fiscal,LastViewedDate,LastReferencedDate,HasOpenActivity,HasOverdueTask,OrderNumber__c,DeliveryInstallationStatus__c,TrackingNumber__c,MainCompetitors__c,CurrentGenerators__c";
		
		for (Notification notification : outboundMessage.getNotifications()) {
			String query = String.format("Select %s From %s Where Id = '%s'", queryString, notification.getSobject().getObject(), notification.getSobject().getObjectId());
			
			builder.addParameter("q", query);
			
			HttpGet get = new HttpGet(builder.build());
			get.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + outboundMessage.getSessionId());
			
			HttpResponse response = client.execute(get);
			
			ObjectMapper objectMapper = new ObjectMapper();
			
			ObjectNode result = objectMapper.readValue(response.getEntity().getContent(), ObjectNode.class);
			
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				if (result.get("records").isArray()) {
					File file = writeFile(notification.getSobject().getId(), result.get("records"));
					writeToBucket(notification.getSobject(), file);
				}
			} else {
				throw new IOException(result.toString());
			}
			
			System.out.println("processed in ms: " + String.valueOf(System.currentTimeMillis() - startTime));
		}
	}
	
	private File writeFile(String id, JsonNode records) throws IOException {
		File file = new File("/tmp/id.json");
		if (!file.exists()) {
			file.createNewFile();
		}
		FileWriter fw = new FileWriter(file.getAbsoluteFile());
		BufferedWriter bw = new BufferedWriter(fw);
		bw.write(records.toString());
		bw.close();
		return file;
	}
	
	private void writeToBucket(Sobject sobject, File file) {
		AmazonS3 s3Client = new AmazonS3Client();
		
		ObjectMetadata metadata = new ObjectMetadata();
		metadata.setContentLength(file.length());
		metadata.setSSEAlgorithm(ObjectMetadata.AES_256_SERVER_SIDE_ENCRYPTION); 
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
		
		String key = sobject.getObject()
				.concat("/")
				.concat(sobject.getObjectId())
				.concat("/")
				.concat(sdf.format(new Date()));
    	
    	PutObjectRequest putObjectRequest = new PutObjectRequest("salesforce-outbound-messages", key, file).withMetadata(metadata);
    	
    	s3Client.putObject(putObjectRequest);
	}
}