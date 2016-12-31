package com.nowellpoint.aws.data;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Executors;

import org.jboss.logging.Logger;

import com.amazonaws.util.IOUtils;
import com.nowellpoint.util.Properties;

public class LogManager {
	
	private static final Logger LOGGER = Logger.getLogger(LogManager.class);
	
	public static void writeLogEntry(String tag, String logEntry) {
		
		Executors.newSingleThreadExecutor().execute(new Runnable() {
			@Override
			public void run() {
				try {
					
					HttpURLConnection connection = (HttpURLConnection) new URL(System.getProperty(Properties.LOGGLY_API_ENDPOINT)
							.concat("/")
							.concat(System.getProperty(Properties.LOGGLY_API_KEY))
							.concat("/")
							.concat(tag)
							.concat("/api")
							.concat("/")
					).openConnection();
					
					connection.setRequestMethod("GET");
					connection.setRequestProperty("content-type", "application/x-www-form-urlencoded");
					connection.setDoOutput(true);
					
					byte[] outputInBytes = logEntry.toString().getBytes("UTF-8");
					OutputStream os = connection.getOutputStream();
					os.write( outputInBytes );    
					os.close();
					
					connection.connect();
					
					if (connection.getResponseCode() != 200) {
						LOGGER.error(IOUtils.toString(connection.getErrorStream()));
					}
				} catch (IOException e) {
					LOGGER.error(e.getMessage(), e);
				}
			}
			
		});	
	}
}