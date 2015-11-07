package com.nowellpoint.aws.http;

import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.List;

public interface HttpResponse {
	
	public int getStatusCode();
	
	public URL getURL();

	public String getEntity() throws IOException;
	
	public <T> T getEntity(Class<T> type) throws IOException;
	
	public Map<String, List<String>> getHeaders();
}