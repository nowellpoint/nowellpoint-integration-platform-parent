package com.nowellpoint.console.entity;

import java.io.Serializable;
import java.util.Date;

public class Connection implements Serializable {
	
	private static final long serialVersionUID = -8557210889178981333L;
	
	private String id;
	
	private String instanceUrl;
	
	private String refreshToken;
	
	private String tokenType;
	
	private String issuedAt;
	
	private String connectedAs;
	
	private Date connectedAt;
	
	private String status;
	
	private Boolean isConnected;
	
	public Connection() {
		
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getInstanceUrl() {
		return instanceUrl;
	}

	public void setInstanceUrl(String instanceUrl) {
		this.instanceUrl = instanceUrl;
	}

	public String getRefreshToken() {
		return refreshToken;
	}

	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}

	public String getTokenType() {
		return tokenType;
	}

	public void setTokenType(String tokenType) {
		this.tokenType = tokenType;
	}

	public String getIssuedAt() {
		return issuedAt;
	}

	public void setIssuedAt(String issuedAt) {
		this.issuedAt = issuedAt;
	}

	public String getConnectedAs() {
		return connectedAs;
	}

	public void setConnectedAs(String connectedAs) {
		this.connectedAs = connectedAs;
	}

	public Date getConnectedAt() {
		return connectedAt;
	}

	public void setConnectedAt(Date connectedAt) {
		this.connectedAt = connectedAt;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Boolean getIsConnected() {
		return isConnected;
	}

	public void setIsConnected(Boolean isConnected) {
		this.isConnected = isConnected;
	}
}