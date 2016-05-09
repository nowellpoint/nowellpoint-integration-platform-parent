package com.nowellpoint.aws.sforce;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;

import com.fasterxml.jackson.databind.JsonNode;
import com.nowellpoint.aws.http.MediaType;
import com.nowellpoint.aws.http.RestResource;

public class SalesforceResource {
	
	public JsonNode getCurrentUser(String serviceEndpoint, String bearerToken) throws MalformedURLException, IOException {
		return RestResource.get(SalesforceUrlFactory.currentUserURL(serviceEndpoint))
				.acceptCharset(StandardCharsets.UTF_8)
				.bearerAuthorization(bearerToken)
				.accept(MediaType.APPLICATION_JSON)
				.execute()
				.getEntity(JsonNode.class);
	}
	
	public JsonNode getCurrentOrganization(String serviceEndpoint, String bearerToken) throws MalformedURLException, IOException {
		return RestResource.get(SalesforceUrlFactory.currentOrganizationURL(serviceEndpoint))
				.acceptCharset(StandardCharsets.UTF_8)
				.bearerAuthorization(bearerToken)
				.accept(MediaType.APPLICATION_JSON)
				.execute()
				.getEntity(JsonNode.class);
	}
	
	public JsonNode describeSObject(String serviceEndpoint, String bearerToken, String sObject) throws MalformedURLException, IOException {
		return RestResource.get(SalesforceUrlFactory.describeURL(serviceEndpoint, sObject))
				.acceptCharset(StandardCharsets.UTF_8)
				.bearerAuthorization(bearerToken)
				.accept(MediaType.APPLICATION_JSON)
				.execute()
				.getEntity(JsonNode.class);
	}
}