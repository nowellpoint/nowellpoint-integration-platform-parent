package com.nowellpoint.aws.http;

import static java.util.Optional.ofNullable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.entity.StringEntity;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.client.config.RequestConfig;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public abstract class HttpRequest {
	
	private ObjectMapper objectMapper;
	private HttpMethod httpMethod;
	private String target;
	private String path;
	//private Map<String,String> headers;
	private List<Header> headers;
	private Map<String,String> parameters;
	//private Map<String,String> queryParameters;
	private List<NameValuePair> queryParameters;
	private Object body;
	
	static {
		HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
			public boolean verify(String hostname, SSLSession sslSession) {
				if (hostname.equals("localhost")) {
					return true;
			    }
			    return false;
			}
		});
	}
	
	public HttpRequest(HttpMethod httpMethod, String target) {
		this.objectMapper = new ObjectMapper();
		this.httpMethod = httpMethod; 
		this.target = target;
		this.path = new String();
		//this.headers = new HashMap<String,String>();
		this.headers = new ArrayList<Header>();
		this.parameters = new HashMap<String,String>();
		this.queryParameters = new ArrayList<NameValuePair>();
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
		headers.add(new BasicHeader(key, value));
		return this;
	}
	
	protected HttpRequest headers(Map<String, String> headers) {
		//this.headers = headers;
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
		//queryParameters.put(key, value);
		queryParameters.add(new BasicNameValuePair(key, value));
		return this;
	}
	
	protected HttpRequest acceptCharset(Charset charset) {
		header(HttpHeaders.ACCEPT_CHARSET, charset.displayName());
		return this;
	}
	
	protected HttpRequest acceptCharset(String charset) {
		header(HttpHeaders.ACCEPT_CHARSET, charset);
		//headers.put(HttpHeaders.ACCEPT_CHARSET, charset);
		return this;
	}
	
	protected HttpRequest contentType(String contentType) {
		header(HttpHeaders.CONTENT_TYPE, contentType);
		//headers.put(HttpHeaders.CONTENT_TYPE, contentType);
		return this;
	}
	
	protected HttpRequest basicAuthorization(String username, String password) {
		//headers.put(HttpHeaders.AUTHORIZATION, "Basic ".concat(new String(Base64.getEncoder().encode(username.concat(":").concat(password).getBytes()))));
		header(HttpHeaders.AUTHORIZATION, "Basic ".concat(new String(Base64.getEncoder().encode(username.concat(":").concat(password).getBytes()))));
		return this;
	}
	
	protected HttpRequest bearerAuthorization(String bearerToken) {
		//headers.put(HttpHeaders.AUTHORIZATION, "Bearer " + bearerToken);
		header(HttpHeaders.AUTHORIZATION, "Bearer " + bearerToken);
		return this;
	}
	
	protected HttpRequest accept(String accept) {
		//headers.put(HttpHeaders.ACCEPT, accept);
		header(HttpHeaders.ACCEPT, accept);
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
		} catch (IOException | URISyntaxException e) {
			throw new HttpRequestException(e);
		}
		return json;
	}
	
	public HttpResponse execute() throws HttpRequestException {
		try {
			return new HttpResponseImpl();
		} catch (IOException | URISyntaxException e) {
			throw new HttpRequestException(e);
		}
	}
	
	private URI buildTarget() throws URISyntaxException{
		StringBuilder sb = new StringBuilder();
		if (target.endsWith("/")) {
			target = target.substring(0, target.length() - 1);
		}
		sb.append(target);
		if (path != null && ! path.trim().isEmpty()) {
			sb.append(path);
		}
//		if (! queryParameters.isEmpty()) {
//			sb.append("?");
//			queryParameters.keySet().forEach(param -> {
//				sb.append(param);
//				sb.append("=");
//				sb.append(queryParameters.get(param));
//				sb.append("&");
//			});
//		}
		
		URIBuilder builder = new URIBuilder()
				.setPath(sb.toString())
				.addParameters(queryParameters);
		
		return builder.build();
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
		
		private URL url;
		
		private InputStream entity;
		
		//private HttpURLConnection connection;
		
		public HttpResponseImpl() throws IOException, URISyntaxException {
			//URL url = buildTarget();
			
			URI uri = buildTarget();
			
			HttpClient httpClient = HttpClients.createDefault();
			HttpUriRequest uriRequest = RequestBuilder.create(httpMethod.toString()).setUri(uri).build();
			uriRequest.setHeaders(headers.toArray(new Header[headers.size()]));
			
			if (! parameters.isEmpty()) {
				
				if (! Optional.ofNullable(uriRequest.getHeaders(HttpHeaders.CONTENT_TYPE)).isPresent()) {
					throw new IOException("Missing content type header");
				}
				
				StringBuilder sb = new StringBuilder();
				parameters.keySet().forEach(param -> {
					sb.append(param);
					sb.append("=");
					if (parameters.get(param) != null) {
						sb.append(parameters.get(param));
					}
					sb.append("&");
				});
				
				body = sb.toString();
				
				HttpEntity httpEntity = new StringEntity(sb.toString());
				
				if ("PUT".equals(httpMethod.toString())) {
					((HttpPut) uriRequest).setEntity(httpEntity);
				}
				
				if ("POST".equals(httpMethod.toString())) {
					((HttpPost) uriRequest).setEntity(httpEntity);
				}
			} 
			
			org.apache.http.HttpResponse httpResponse = httpClient.execute(uriRequest);
			
			statusCode = httpResponse.getStatusLine().getStatusCode();
			entity = httpResponse.getEntity().getContent();
			url = uri.toURL();
			
//			if ("https".equals(url.getProtocol())) {
//				connection = (HttpsURLConnection) url.openConnection();
//			} else {
//				connection = (HttpURLConnection) url.openConnection();
//			}
//			
//			connection.setRequestMethod(httpMethod.toString());
//			
//			headers.keySet().forEach(key -> {
//				connection.setRequestProperty(key, headers.get(key));
//			});
//			
//			if (! parameters.isEmpty()) {
//				StringBuilder sb = new StringBuilder();
//				parameters.keySet().forEach(param -> {
//					sb.append(param);
//					sb.append("=");
//					if (parameters.get(param) != null) {
//						sb.append(parameters.get(param));
//					}
//					sb.append("&");
//				});
//				
//				body = sb.toString();
//			} 
//			
//			if (ofNullable(body).isPresent()) {
//				
//				if (! Optional.ofNullable(headers.get(HttpHeaders.CONTENT_TYPE)).isPresent()) {
//					throw new IOException("Missing content type header");
//				}
//				
//				byte[] bytes = null;
//				
//				if (headers.get(HttpHeaders.CONTENT_TYPE).equals(MediaType.APPLICATION_JSON)) {
//					bytes = objectMapper.writeValueAsString(body).getBytes();
//				} else {
//					bytes = String.valueOf(body).getBytes();
//				}
//				
//				connection.setDoOutput(true);
//				connection.setRequestProperty(HttpHeaders.CONTENT_LENGTH, Integer.toString(bytes.length));
//				
//				OutputStream os = connection.getOutputStream();
//				os.write( bytes );    
//				os.close();
//			}
//
//			connection.connect();
//			
//			statusCode = connection.getResponseCode();
//			url = connection.getURL();
//			
//			if (statusCode < 400) {
//				entity = connection.getInputStream();
//			} else {
//				entity = connection.getErrorStream();
//			}
		}
	
		public int getStatusCode() {
			return statusCode;
		}
		
		public URL getURL() {
			return url;
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
		
		public Map<String, String> getHeaders() {
			return headers.stream().collect(Collectors.toMap(Header::getName, Header::getValue));
		}
	}
}