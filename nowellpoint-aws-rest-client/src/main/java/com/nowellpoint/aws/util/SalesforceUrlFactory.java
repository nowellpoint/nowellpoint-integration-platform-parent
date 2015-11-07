package com.nowellpoint.aws.util;

import java.net.MalformedURLException;
import java.net.URL;

public class SalesforceUrlFactory {
	
	public static URL describeURL(String serviceEndpoint, String sObject) throws MalformedURLException {
		return new URL(dataURI(serviceEndpoint).concat("/").concat(getVersion(serviceEndpoint)).concat("/").concat("sobjects").concat("/").concat(sObject));		
	}
	
	public static URL queryURL(String serviceEndpoint) throws MalformedURLException {
		return new URL(dataURI(serviceEndpoint).concat("/").concat(getVersion(serviceEndpoint)).concat("/").concat("query"));
	}
	
	public static URL currentUserURL(String serviceEndpoint) throws MalformedURLException {
		return new URL(apexRestURI(serviceEndpoint).concat("/user"));
	}
	
	public static URL currentOrganizationURL(String serviceEndpoint) throws MalformedURLException {
		return new URL(apexRestURI(serviceEndpoint).concat("/organization"));
	}
	
	private static String dataURI(String serviceEndpoint) {
		return baseURI(serviceEndpoint).concat("/").concat("data");
	}
	
	private static String apexRestURI(String serviceEndpoint) {
		return baseURI(serviceEndpoint).concat("/").concat("apexrest/nowellpoint");
	}
	
	private static String baseURI(String serviceEndpoint) {
		return serviceEndpoint.substring(0, serviceEndpoint.indexOf("Soap") - 1);
	}
 	
	private static String getVersion(String serviceEndpoint) {
		int startPosition;
		if (serviceEndpoint.contains("/u/")) {
			startPosition = serviceEndpoint.indexOf("/u/") + 3;
		} else {
			startPosition = serviceEndpoint.indexOf("/c/") + 3;
		}
		
		int endPosition = startPosition + 4;
		return "v".concat(serviceEndpoint.substring(startPosition, endPosition));
	}
}