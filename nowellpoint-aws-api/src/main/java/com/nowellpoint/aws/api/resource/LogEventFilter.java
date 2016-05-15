package com.nowellpoint.aws.api.resource;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Executors;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.Context;
import javax.ws.rs.ext.Provider;

import org.jboss.logging.Logger;

import com.amazonaws.util.IOUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.nowellpoint.aws.api.dto.idp.Token;
import com.nowellpoint.aws.model.admin.Properties;
import com.nowellpoint.aws.tools.TokenParser;

@Provider
public class LogEventFilter implements ContainerResponseFilter {
	
	private static final Logger LOG = Logger.getLogger(LogEventFilter.class);
	
	@Context
	private HttpServletRequest httpRequest;
	
	@Override
	public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {
		
		final String subject = getSubject(requestContext, responseContext);
		final String address = httpRequest.getLocalName();
		final String path = httpRequest.getPathInfo().concat(httpRequest.getQueryString() != null ? "?".concat(httpRequest.getQueryString()) : "");
		final Integer statusCode = responseContext.getStatus();
		final String statusInfo = responseContext.getStatusInfo().toString();
		final String requestMethod = requestContext.getMethod();
		
		Executors.newSingleThreadExecutor().execute(new Runnable() {
			@Override
			public void run() {
				
				ObjectNode node = new ObjectMapper().createObjectNode()
						.put("subject", subject)
						.put("date", System.currentTimeMillis())
						.put("address", address)
						.put("method", requestMethod)
						.put("path", path)
						.put("statusCode", statusCode)
						.put("statusInfo", statusInfo);
				
				HttpURLConnection connection;
				try {
					connection = (HttpURLConnection) new URL(System.getProperty(Properties.LOGGLY_API_ENDPOINT)
							.concat("/")
							.concat(System.getProperty(Properties.LOGGLY_API_KEY))
							.concat("/tag")
							.concat("/api")
							.concat("/")
					).openConnection();
					
					connection.setRequestMethod("GET");
					connection.setRequestProperty("content-type", "application/x-www-form-urlencoded");
					connection.setDoOutput(true);
					
					byte[] outputInBytes = node.toString().getBytes("UTF-8");
					OutputStream os = connection.getOutputStream();
					os.write( outputInBytes );    
					os.close();
					
					connection.connect();
					
					if (connection.getResponseCode() != 200) {
						LOG.error(IOUtils.toString(connection.getErrorStream()));
					}
				} catch (IOException e) {
					LOG.error(e.getMessage(), e);
				}
			}
		});
	}
	
	private String getSubject(ContainerRequestContext requestContext, ContainerResponseContext responseContext) {
		if (responseContext.getEntity() != null && responseContext.getEntity().getClass().isAssignableFrom(Token.class)) {
			Token token = (Token) responseContext.getEntity();
			return TokenParser.parseToken(token.getAccessToken());
		} else {
			return requestContext.getSecurityContext().getUserPrincipal() != null ? requestContext.getSecurityContext().getUserPrincipal().getName() : null;
		}
	}
}