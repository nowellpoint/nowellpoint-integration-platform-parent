package com.nowellpoint.sforce;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.apache.http.client.ClientProtocolException;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.events.DynamodbEvent;
import com.nowellpoint.sforce.model.OutboundMessage;
import com.nowellpoint.sforce.model.OutboundMessageResult;

public class OutboundMessageHandler {
	
	private static DynamoDBMapper mapper = new DynamoDBMapper(new AmazonDynamoDBClient());
	
	public String handleEvent(DynamodbEvent event, Context context) {
		
		Long startTime = System.currentTimeMillis();
		
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
				outboundMessage.setProcessDuration(System.currentTimeMillis() - startTime);
				outboundMessage.setSessionId(null);
				mapper.save(outboundMessage);
			}
		});
		
		return "ok";
	}
	
	private void process(OutboundMessage outboundMessage) throws ClientProtocolException, IOException, URISyntaxException, ExecutionException, InterruptedException {
		
		Set<Callable<OutboundMessageResult>> tasks = new LinkedHashSet<Callable<OutboundMessageResult>>();
		
		outboundMessage.getNotifications().stream().forEach(notification -> tasks.add(new OutboundMessageHandlerTask(
				mapper, 
				notification, 
				outboundMessage.getSessionId(), 
				outboundMessage.getOrganizationId(), 
				outboundMessage.getPartnerUrl())));
		
		ExecutorService executor = Executors.newFixedThreadPool(tasks.size());
		
		List<OutboundMessageResult> results = new ArrayList<OutboundMessageResult>();
		
		List<Future<OutboundMessageResult>> futures = executor.invokeAll(tasks);
		executor.shutdown();
		executor.awaitTermination(30, TimeUnit.SECONDS);
		
		for (Future<OutboundMessageResult> future : futures) {
			results.add(future.get());
		}
		
		outboundMessage.setResults(results);
	}
}