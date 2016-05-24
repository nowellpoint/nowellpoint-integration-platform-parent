package com.nowellpoint.sforce;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.Date;
import java.time.Instant;

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
import com.amazonaws.util.IOUtils;
import com.nowellpoint.sforce.model.Notification;
import com.nowellpoint.sforce.model.OutboundMessage;

public class OutboundMessageHandler {
	
	private static DynamoDBMapper mapper = new DynamoDBMapper(new AmazonDynamoDBClient());

	public String handleEvent(DynamodbEvent event, Context context) {
		
		LambdaLogger logger = context.getLogger();
		
		event.getRecords().stream().filter(record -> "INSERT".equals(record.getEventName())).forEach(record -> {
			
			logger.log("DynamodbEvent received...Event Id: "
					.concat(record.getEventID())
					.concat(" Event Name: " + record.getEventName()));
			
			String organizationId = record.getDynamodb().getKeys().get("OrganizationId").getS();
			String key = record.getDynamodb().getKeys().get("Key").getS();
			
			OutboundMessage outboundMessage = mapper.load(OutboundMessage.class, organizationId, key);
			
			try {
				process(outboundMessage);
				outboundMessage.setStatus("PROCESSED");
			} catch (Exception e) {
				outboundMessage.setStatus("FAILED");
				outboundMessage.setErrorMessage(e.getMessage());
			} finally {
				outboundMessage.setProcessedDate(Date.from(Instant.now()));
				outboundMessage.setDuration(System.currentTimeMillis() - outboundMessage.getReceivedDate().getTime());
				outboundMessage.setSessionId(null);
				mapper.save(outboundMessage);
			}
		});
		
		return "ok";
	}
	
	private void process(OutboundMessage outboundMessage) throws ClientProtocolException, IOException, URISyntaxException {
		
		HttpClient client = HttpClientBuilder.create().build();
		
		String url = outboundMessage
				.getPartnerUrl()
				.substring(0, outboundMessage
						.getPartnerUrl()
						.lastIndexOf("/") + 1)
				.replace("/Soap/u/", "/data/v")
				.concat("query");
		
		URIBuilder builder = new URIBuilder(url);
		
		for (Notification notification : outboundMessage.getNotifications()) {
			String query = String.format("SELECT Id, Name From %s Where Id = '%s'", notification.getSobject().getObject(), notification.getSobject().getObjectId());
			
			builder.addParameter("q", query);
			
			HttpGet get = new HttpGet(builder.build());
			get.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + outboundMessage.getSessionId());
			
			HttpResponse response = client.execute(get);

			String result = IOUtils.toString(response.getEntity().getContent());
			
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				System.out.println(result);
			} else {
				throw new IOException(result);
			}
		}
	}
}