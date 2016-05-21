package com.nowellpoint.sforce;

import java.net.URLEncoder;
import java.sql.Date;
import java.time.Instant;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.events.DynamodbEvent;
import com.amazonaws.util.IOUtils;
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
			
			process(outboundMessage);
			
			outboundMessage.setStatus("PROCESSED");
			outboundMessage.setProcessedDate(Date.from(Instant.now()));
			
			mapper.save(outboundMessage);
			
		});
		
		return "ok";
	}
	
	private void process(OutboundMessage outboundMessage) {
		//https://na45.salesforce.com/services/Soap/u/36.0/00D300000000lnE
		//https://na1.salesforce.com/services/data/v20.0/query/?q
		HttpClient client = HttpClientBuilder.create().build();
		outboundMessage.getNotifications().stream().forEach(notification -> {
			String url = outboundMessage.getPartnerUrl().substring(0, outboundMessage.getPartnerUrl().lastIndexOf("/") + 1).replace("/Soap/u/", "/data/v").concat("?q=");
			String query = String.format("SELECT Id, Name From %s Where Id = '%s'", notification.getSobject().getObject(), notification.getSobject().getObjectId());
			try {
				url.concat(URLEncoder.encode(query, "UTF-8"));
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			System.out.println(url);
			HttpGet get = new HttpGet(url);
			get.addHeader("Authorization", "Bearer: " + outboundMessage.getSessionId());
			try {
				HttpResponse response = client.execute(get);
				String result = IOUtils.toString(response.getEntity().getContent());
				System.out.println(result);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
	}
}