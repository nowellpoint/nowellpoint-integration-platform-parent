package com.nowellpoint.aws.http;

import java.net.URL;
import java.util.Map;
import java.util.List;

public interface HttpResponse {
	
	public int getStatusCode();
	
	public URL getURL();

	public String getAsString() throws HttpRequestException;
	
	public <T> T getEntity(Class<T> type) throws HttpRequestException;
	
	public <T> List<T> getEntityList(Class<T> type) throws HttpRequestException;
	
	public Map<String, List<String>> getHeaders();
}