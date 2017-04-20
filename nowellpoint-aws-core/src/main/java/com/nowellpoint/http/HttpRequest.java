package com.nowellpoint.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TimeZone;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContextBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;

public abstract class HttpRequest {
	
	private static final Logger LOG = Logger.getLogger(HttpRequest.class.getName());
	
	private ObjectMapper objectMapper;
	private HttpMethod httpMethod;
	private String target;
	private String path;
	private List<Header> headers;
	private Map<String,String> parameters;
	private List<NameValuePair> queryParameters;
	private Object body;
	
	public HttpRequest(HttpMethod httpMethod, String target) {
		this.objectMapper = new ObjectMapper();
		this.httpMethod = httpMethod; 
		this.target = target;
		this.path = new String();
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
		headers.keySet().stream().forEach(key -> {
			header(key, headers.get(key));
		});
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
		queryParameters.add(new BasicNameValuePair(key, value));
		return this;
	}
	
	protected HttpRequest acceptCharset(Charset charset) {
		header(HttpHeaders.ACCEPT_CHARSET, charset.displayName());
		return this;
	}
	
	protected HttpRequest acceptCharset(String charset) {
		header(HttpHeaders.ACCEPT_CHARSET, charset);
		return this;
	}
	
	protected HttpRequest contentType(String contentType) {
		header(HttpHeaders.CONTENT_TYPE, contentType);
		return this;
	}
	
	protected HttpRequest basicAuthorization(String username, String password) {
		header(HttpHeaders.AUTHORIZATION, "Basic ".concat(new String(Base64.getEncoder().encode(username.concat(":").concat(password).getBytes()))));
		return this;
	}
	
	protected HttpRequest bearerAuthorization(String bearerToken) {
		header(HttpHeaders.AUTHORIZATION, "Bearer " + bearerToken);
		return this;
	}
	
	protected HttpRequest accept(String accept) {
		header(HttpHeaders.ACCEPT, accept);
		return this;
	}

	public HttpRequest body(Object body) {
		this.body = body;
		return this;
	}
	
	public HttpResponse execute() throws HttpRequestException {
		try {
			return new HttpResponseImpl();
		} catch (IOException | URISyntaxException | 
				KeyManagementException | NoSuchAlgorithmException | 
				KeyStoreException e) {
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
		
		public HttpResponseImpl() throws IOException, URISyntaxException, KeyManagementException, NoSuchAlgorithmException, KeyStoreException {
			
			URI uri = buildTarget();
			
			SSLContextBuilder builder = new SSLContextBuilder();
		    builder.loadTrustMaterial(null, new TrustSelfSignedStrategy());
		    
		    SSLConnectionSocketFactory socketFactory = new SSLConnectionSocketFactory(builder.build());
		    CloseableHttpClient httpClient = HttpClients.custom().setSSLSocketFactory(socketFactory).build();
			
			CloseableHttpResponse httpResponse = null;
			
			LOG.info(String.format("[Nowellpoint] [%1$tY-%1$tm-%1$td %tT %2s] %3s %4s", new Date(), TimeZone.getDefault().getID(), httpMethod, uri.getPath()));
			
			if (HttpMethod.POST.equals(httpMethod)) {
				HttpPost post = new HttpPost(uri);
				post.setHeaders(addHeaders());
				if (Optional.ofNullable(post.getFirstHeader(HttpHeaders.CONTENT_TYPE)).isPresent()) {
					HttpEntity entity = addBody(post.getFirstHeader(HttpHeaders.CONTENT_TYPE).getValue());
			        post.setEntity(entity);
				}
		        httpResponse = httpClient.execute(post);
			} else if (HttpMethod.PUT.equals(httpMethod)) {
				HttpPut put = new HttpPut(uri);
				put.setHeaders(addHeaders());
				if (! Optional.ofNullable(put.getFirstHeader(HttpHeaders.CONTENT_TYPE)).isPresent()) {
					throw new IOException("Missing content type header");
				}
		        HttpEntity entity = addBody(put.getFirstHeader(HttpHeaders.CONTENT_TYPE).getValue());
		        put.setEntity(entity);
		        httpResponse = httpClient.execute(put);
			} else if (HttpMethod.GET.equals(httpMethod)) {
				HttpGet get = new HttpGet(uri);
				get.setHeaders(addHeaders());
		        httpResponse = httpClient.execute(get);
			} else if (HttpMethod.DELETE.equals(httpMethod)) {
				HttpDelete delete = new HttpDelete(uri);
				delete.setHeaders(addHeaders());
		        httpResponse = httpClient.execute(delete);
			} else {
				System.out.println("unsupported method exception");
				throw new IOException("unsupported method exception");
			}
			
			statusCode = httpResponse.getStatusLine().getStatusCode();
			url = uri.toURL();
			
			if (httpResponse.getEntity() != null) {
				entity = httpResponse.getEntity().getContent();
			}
			
			headers = Arrays.asList(httpResponse.getAllHeaders());
		}
	
		@Override
		public int getStatusCode() {
			return statusCode;
		}
		
		@Override
		public URL getURL() {
			return url;
		}
	
		@Override
		public String getAsString() throws HttpRequestException {
			try {
				return parseResponse(entity);
			} catch (IOException e) {
				throw new HttpRequestException(e);
			}
		}
		
		@Override
		public <T> T getEntity(Class<T> type) throws HttpRequestException {
			try {
				return objectMapper.readValue(entity, type);
			} catch (IOException e) {
				throw new HttpRequestException(e);
			}
		}
		
		@Override
		public <T> List<T> getEntityList(Class<T> type) throws HttpRequestException {
			try {
				return objectMapper.readValue(entity, objectMapper.getTypeFactory().constructCollectionType(List.class, type));
			} catch (IOException e) {
				throw new HttpRequestException(e);
			}
		}
		
		@Override
		public Map<String, String> getHeaders() {
			return headers.stream().collect(Collectors.toMap(Header::getName, Header::getValue));
		}
		
		private Header[] addHeaders() {
			return headers.toArray(new Header[headers.size()]);
		}
		
		private HttpEntity addBody(String contentType) throws IOException {
			if (! parameters.isEmpty()) {
				
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
			}
			
			HttpEntity httpEntity = null;
			
			if (Optional.ofNullable(body).isPresent()) {
				
				byte[] bytes = null;
				
				if (contentType.equals(MediaType.APPLICATION_JSON)) {
					bytes = objectMapper.writeValueAsString(body).getBytes();
				} else {
					bytes = String.valueOf(body).getBytes();
				}
				
				httpEntity = new ByteArrayEntity(bytes);
			}
			
			return httpEntity;
		}
	}
}