package com.nowellpoint.aws.lambda.scheduled;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.List;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.amazonaws.services.logs.AWSLogs;
import com.amazonaws.services.logs.AWSLogsClient;
import com.amazonaws.services.logs.model.DeleteLogStreamRequest;
import com.amazonaws.services.logs.model.DescribeLogGroupsResult;
import com.amazonaws.services.logs.model.DescribeLogStreamsRequest;
import com.amazonaws.services.logs.model.DescribeLogStreamsResult;
import com.amazonaws.services.logs.model.GetLogEventsRequest;
import com.amazonaws.services.logs.model.GetLogEventsResult;
import com.amazonaws.services.logs.model.OutputLogEvent;
import com.amazonaws.util.json.JSONObject;
import com.nowellpoint.aws.util.Configuration;

public class LogEventConsumer implements RequestStreamHandler {
	
	private static AWSLogs logsClient = new AWSLogsClient();

	@Override
	public void handleRequest(InputStream inputStream, OutputStream outputStream, Context context) throws IOException {
		DescribeLogGroupsResult describeLogGroupsResult = logsClient.describeLogGroups();
		describeLogGroupsResult.getLogGroups().forEach(logGroup -> {
			
			DescribeLogStreamsRequest describeLogStreamsRequest = new DescribeLogStreamsRequest().withLogGroupName(logGroup.getLogGroupName());
			DescribeLogStreamsResult describeLogStreamsResult = logsClient.describeLogStreams(describeLogStreamsRequest);
			describeLogStreamsResult.getLogStreams().forEach(stream -> {
				
				StringBuilder logEntry = new StringBuilder();
				
				GetLogEventsRequest getLogEventsRequest = new GetLogEventsRequest().withStartFromHead(Boolean.TRUE)
						.withLogGroupName(describeLogStreamsRequest.getLogGroupName())
						.withLogStreamName(stream.getLogStreamName());
				
				while (true) {
					
					GetLogEventsResult getLogEventsResult = logsClient.getLogEvents(getLogEventsRequest);
					List<OutputLogEvent> events = getLogEventsResult.getEvents();
					
					if (events.size() == 0) {
						break;
					}
					
					events.forEach(event -> {
						try {
							logEntry.append(new JSONObject().put("timestamp", new Date(event.getTimestamp()))
									.put("logGroupName", describeLogStreamsRequest.getLogGroupName())
									.put("logStreamName", stream.getLogStreamName())
									.put("message", event.getMessage())
									.put("ingestionTime", new Date(event.getIngestionTime()).toString()));
						} catch (Exception e) {
							e.printStackTrace();
						}
					});
					
					getLogEventsRequest = new GetLogEventsRequest().withNextToken(getLogEventsResult.getNextForwardToken())
							.withLogGroupName(describeLogStreamsRequest.getLogGroupName())
							.withLogStreamName(stream.getLogStreamName());
						
					getLogEventsResult = logsClient.getLogEvents(getLogEventsRequest);
				}
				
				/**
				 * 
				 * execute the GET to loggly
				 * 
				 */
				
				try {
					HttpURLConnection connection = (HttpURLConnection) new URL("http://logs-01.loggly.com/inputs/"
							.concat(Configuration.getLogglyApiKey())
							.concat("/tag/")
							.concat(describeLogStreamsRequest.getLogGroupName())
							.concat("/")).openConnection();
					
					connection.setRequestMethod("GET");
					connection.setRequestProperty("content-type", "text/plain");
					connection.setDoOutput(true);
					
					byte[] outputInBytes = logEntry.toString().getBytes("UTF-8");
					OutputStream os = connection.getOutputStream();
					os.write( outputInBytes );    
					os.close();
					
					connection.connect();
					
					if (connection.getResponseCode() == 200) {
						DeleteLogStreamRequest deleteLogEventsRequest = new DeleteLogStreamRequest().withLogGroupName(describeLogStreamsRequest.getLogGroupName()).withLogStreamName(stream.getLogStreamName());
						logsClient.deleteLogStream(deleteLogEventsRequest);
					}
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			});
		});
	}
}