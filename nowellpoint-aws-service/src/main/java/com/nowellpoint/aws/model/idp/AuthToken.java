package com.nowellpoint.aws.model.idp;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AuthToken implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6394639624717926877L;
	
	@JsonProperty(value="href")
	private String href;
	
	@JsonProperty(value="createdAt")
	private String createdAt;
	
	@JsonProperty(value="jwt")
	private String jwt;
	
	@JsonProperty(value="expandedJwt")
	private ExpandedJwt expandedJwt;
	
	@JsonProperty(value="account")
	private Account account;
	
	@JsonProperty(value="application")
	private Application application;
	
	@JsonProperty(value="tenant")
	private Tenant tenant;
	
	public AuthToken() {
		
	}

	public String getHref() {
		return href;
	}

	public void setHref(String href) {
		this.href = href;
	}

	public String getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(String createdAt) {
		this.createdAt = createdAt;
	}

	public String getJwt() {
		return jwt;
	}

	public void setJwt(String jwt) {
		this.jwt = jwt;
	}

	public ExpandedJwt getExpandedJwt() {
		return expandedJwt;
	}

	public void setExpandedJwt(ExpandedJwt expandedJwt) {
		this.expandedJwt = expandedJwt;
	}

	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}

	public Application getApplication() {
		return application;
	}

	public void setApplication(Application application) {
		this.application = application;
	}

	public Tenant getTenant() {
		return tenant;
	}

	public void setTenant(Tenant tenant) {
		this.tenant = tenant;
	}
}