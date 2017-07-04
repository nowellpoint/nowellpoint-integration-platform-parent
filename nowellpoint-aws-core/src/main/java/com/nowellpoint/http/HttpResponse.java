package com.nowellpoint.http;

import java.io.InputStream;
import java.net.URL;
import java.util.Map;
import java.util.List;

public interface HttpResponse {
	
	public int getStatusCode();
	
	public URL getURL();

	public String getAsString() throws HttpRequestException;
	
	public InputStream getEntity();
	
	public <T> T getEntity(Class<T> type) throws HttpRequestException;
	
	public <T> List<T> getEntityList(Class<T> type) throws HttpRequestException;
	
	public Map<String, String> getHeaders();
}