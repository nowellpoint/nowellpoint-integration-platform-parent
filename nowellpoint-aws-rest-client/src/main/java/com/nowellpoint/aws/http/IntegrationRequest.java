package com.nowellpoint.aws.http;


import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@JsonIgnoreProperties(ignoreUnknown=true)
public class IntegrationRequest {
	
	private static ObjectMapper objectMapper;
	private Map<String, String> parameterMap;	
	private Headers headers;
	private String stage;
	private String requestId;
	private String resourcePath;
	private String resourceId;
	private String httpMethod;
	private String sourceIp;
	private String userAgent;
	private String body;
	
	public IntegrationRequest() {
		objectMapper = new ObjectMapper();
		parameterMap = new HashMap<String,String>();
		headers = new Headers();
	}
	
	public void setHeaders(Headers headers) {
		this.headers = headers;
	}
	
	public Headers getHeaders() {
		return headers;
	}

	public String getStage() { 
		return stage; 
	}
	
	public void setStage(String stage) {
		this.stage = stage;
	}

	public String getRequestId() { 
		return requestId; 
	}
	
	public void setResourceId(String resourceId) { 
		this.resourceId = resourceId; 
	}
	
	public void setReqeustId(String requestId) { 
		this.requestId = requestId; 
	}

	public String getResourcePath() { 
		return resourcePath; 
	}
	
	public void setResourcePath(String resourcePath) { 
		this.resourcePath = resourcePath; 
	}
	
	public String getResourceId() { 
		return resourceId; 
	}

	public String getHttpMethod() { 
		return httpMethod; 
	}
	
	public void setHttpMethod(String httpMethod) { 
		this.httpMethod = httpMethod; 
	}

	public String getSourceIp() { 
		return sourceIp; 
	}
	
	public void setSourceId(String sourceIp) { 
		this.sourceIp = sourceIp; 
	}

	public String getUserAgent() { 
		return userAgent; 
	}
	
	public void setUserAgent(String userAgent) { 
		this.userAgent = userAgent; 
	}

	public Map<String, String> getParameters() {
		return parameterMap;
	}
	
	public void setQueryParams(String queryParams) { 
		parameterMap.putAll(parseParameters(queryParams));
	}
	
	public void setPathParams(String pathParams) { 
		parameterMap.putAll(parseParameters(pathParams));
	}

	public String getBody() {
		return body;
	}
	
	public void setBody(String body) {
		this.body = body;
		parameterMap.putAll(parseParameters(body));
	}
	
	@JsonIgnore
	public <T> T getBody(Class<T> type) throws IOException {
		return objectMapper.readValue(body, type);
	}

	@JsonIgnore
	public String getParameter(String parameterName) {
		return parameterMap.get(parameterName);
	}
	
	public String asJson() throws JsonProcessingException {
		return objectMapper.writeValueAsString(this);
	}
	
	private Map<String, String> parseParameters(String parameterString) {
		return URLEncodedUtils.parse(parameterString.replace("{", "").replace("}", ""), StandardCharsets.UTF_8, '&')
				.stream()
				.collect(Collectors.toMap(NameValuePair::getName, param -> param.getValue()));
	}
}