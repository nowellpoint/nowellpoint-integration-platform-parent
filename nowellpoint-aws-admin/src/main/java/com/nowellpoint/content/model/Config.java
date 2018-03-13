package com.nowellpoint.content.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Config {
	
	private Mongo mongo;
	private Loggly loggly;
	private Salesforce salesforce;
	private Okta okta;
	private Sendgrid sendgrid;
	private Redis redis;
	private Braintree braintree;
	
	public Config() {

	}
	
	public Mongo getMongo() {
		return mongo;
	}
	
	public Loggly getLoggly() {
		return loggly;
	}
	
	public Salesforce getSalesforce() {
		return salesforce;
	}
	
	public Okta getOkta() {
		return okta;
	}
	
	public Sendgrid getSendgrid() {
		return sendgrid;
	}
	
	public Redis getRedis() {
		return redis;
	}
	
	public Braintree getBraintree() {
		return braintree;
	}
}