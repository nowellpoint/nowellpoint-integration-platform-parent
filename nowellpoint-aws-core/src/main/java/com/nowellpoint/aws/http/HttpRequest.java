package com.nowellpoint.aws.http;

import static java.util.Optional.ofNullable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Base64;

import javax.net.ssl.HttpsURLConnection;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public abstract class HttpRequest {
	
	private ObjectMapper objectMapper;
	private HttpMethod httpMethod;
	private String target;
	private String path;
	private Map<String,String> headers;
	private Map<String,String> parameters;
	private Map<String,String> queryParameters;
	private Object body;
	
	public HttpRequest(HttpMethod httpMethod, String target) {
		this.objectMapper = new ObjectMapper();
		this.httpMethod = httpMethod; 
		this.target = target;
		this.path = new String();
		this.headers = new HashMap<String,String>();
		this.parameters = new HashMap<String,String>();
		this.queryParameters = new HashMap<String,String>();
	}
	
	protected HttpRequest path(String path) {
		if (this.path.endsWith("/")) {
			this.path = new StringBuilder(this.path).append(path).toString();
		} else {
			this.path = new StringBuilder(this.path).append("/").append(path).toString();
		}
		return this;
	}

	protected HttpRequest header(String key, String value) {
		headers.put(key, value);
		return this;
	}
	
	protected HttpRequest headers(Map<String, String> headers) {
		this.headers = headers;
		return this;
	}
	
	protected HttpRequest parameter(String key, String value) {
		parameters.put(key, value);
		return this;
	}
	
	protected HttpRequest parameter(String key, Boolean value) {
		parameters.put(key, String.valueOf(value));
		return this;
	}
	
	protected HttpRequest parameters(Map<String, String> parameters) {
		this.parameters = parameters;
		return this;
	}
	
	protected HttpRequest queryParameter(String key, String value) {
		queryParameters.put(key, value);
		return this;
	}
	
	protected HttpRequest acceptCharset(Charset charset) {
		headers.put(HttpHeaders.ACCEPT_CHARSET, charset.displayName());
		return this;
	}
	
	protected HttpRequest acceptCharset(String charset) {
		headers.put(HttpHeaders.ACCEPT_CHARSET, charset);
		return this;
	}
	
	protected HttpRequest contentType(String contentType) {
		headers.put(HttpHeaders.CONTENT_TYPE, contentType);
		return this;
	}
	
	protected HttpRequest basicAuthorization(String username, String password) {
		headers.put(HttpHeaders.AUTHORIZATION, "Basic ".concat(new String(Base64.getEncoder().encode(username.concat(":").concat(password).getBytes()))));
		return this;
	}
	
	protected HttpRequest bearerAuthorization(String bearerToken) {
		headers.put(HttpHeaders.AUTHORIZATION, "Bearer " + bearerToken);
		return this;
	}
	
	protected HttpRequest accept(String accept) {
		headers.put(HttpHeaders.ACCEPT, accept);
		return this;
	}

	public HttpRequest body(Object body) {
		this.body = body;
		return this;
	}

	@Deprecated
	public JsonNode asJson() throws HttpRequestException {
		JsonNode json;
		try {
			HttpResponse response = new HttpResponseImpl();
			json = objectMapper.readValue(response.getAsString(), JsonNode.class);
		} catch (IOException e) {
			throw new HttpRequestException(e);
		}
		return json;
	}
	
	public HttpResponse execute() throws HttpRequestException {
		try {
			return new HttpResponseImpl();
		} catch (IOException e) {
			throw new HttpRequestException(e);
		}
	}
	
	private URL buildTarget() throws MalformedURLException{
		StringBuilder sb = new StringBuilder();
		if (target.endsWith("/")) {
			target = target.substring(0, target.length() - 1);
		}
		sb.append(target);
		if (path != null && ! path.trim().isEmpty()) {
			sb.append(path);
		}
		if (! queryParameters.isEmpty()) {
			sb.append("?");
			queryParameters.keySet().forEach(param -> {
				sb.append(param);
				sb.append("=");
				sb.append(queryParameters.get(param));
				sb.append("&");
			});
		}
		
		return new URL(sb.toString());
	}
	
	private String parseResponse(InputStream response) throws IOException {
		StringBuilder sb = new StringBuilder();
		String line = null;
		BufferedReader br = new BufferedReader(new InputStreamReader(response));
		while ((line = br.readLine()) != null) {
			sb.append(line); 
		}
		return sb.toString();
	}
	
	private class HttpResponseImpl implements HttpResponse {
		
		private int statusCode;
		
		private InputStream entity;
		
		private HttpURLConnection connection;
		
		public HttpResponseImpl() throws IOException {
			URL url = buildTarget();
			if ("https".equals(url.getProtocol())) {
				connection = (HttpsURLConnection) url.openConnection();
			} else {
				connection = (HttpURLConnection) url.openConnection();
			}
			
			connection.setRequestMethod(httpMethod.toString());
			
			headers.keySet().forEach(key -> {
				connection.setRequestProperty(key, headers.get(key));
			});
			
			if (! parameters.isEmpty()) {
				StringBuilder sb = new StringBuilder();
				parameters.keySet().forEach(param -> {
					sb.append(param);
					sb.append("=");
					sb.append(parameters.get(param));
					sb.append("&");
				});
				
				body = sb.toString();
			} 
			
			if (ofNullable(body).isPresent()) {
								
				byte[] bytes = null;
				
				if (body.getClass().isAssignableFrom(String.class)) {
					bytes = String.valueOf(body).getBytes();
				} else {
					if (! Optional.ofNullable(headers.get(HttpHeaders.CONTENT_TYPE)).isPresent()) {
						throw new IOException("Missing content type header");
					}
					
					if (headers.get(HttpHeaders.CONTENT_TYPE).equals(MediaType.APPLICATION_JSON)) {
						bytes = objectMapper.writeValueAsString(body).getBytes();
					}
				}
				
				connection.setDoOutput(true);
				connection.setRequestProperty("Content-Length", Integer.toString(bytes.length));
				
				OutputStream os = connection.getOutputStream();
				os.write( bytes );    
				os.close();
			}

			connection.connect();
			
			statusCode = connection.getResponseCode();
			url = connection.getURL();
			
			if (statusCode < 400) {
				entity = connection.getInputStream();
			} else {
				entity = connection.getErrorStream();
			}
		}
	
		public int getStatusCode() {
			return statusCode;
		}
		
		public URL getURL() {
			return connection.getURL();
		}
	
		public String getAsString() throws HttpRequestException {
			try {
				return parseResponse(entity);
			} catch (IOException e) {
				throw new HttpRequestException(e);
			}
		}
		
		public <T> T getEntity(Class<T> type) throws HttpRequestException {
			try {
				return objectMapper.readValue(entity, type);
			} catch (IOException e) {
				throw new HttpRequestException(e);
			}
		}
		
		public <T> List<T> getEntityList(Class<T> type) throws HttpRequestException {
			try {
				return objectMapper.readValue(entity, objectMapper.getTypeFactory().constructCollectionType(List.class, type));
			} catch (IOException e) {
				throw new HttpRequestException(e);
			}
		}
		
		public Map<String, List<String>> getHeaders() {
			return connection.getHeaderFields();
		}
	}
}