package com.nowellpoint.api.model.document;

import java.util.Date;

import com.nowellpoint.mongodb.annotation.Document;
import com.nowellpoint.mongodb.annotation.EmbedOne;
import com.nowellpoint.mongodb.annotation.Reference;
import com.nowellpoint.mongodb.document.MongoDocument;

@Document(collectionName="connectors")
public class Connector extends MongoDocument {

	/**
	 * 
	 */
	
	private static final long serialVersionUID = 3080738254802931154L;
	
	@Reference(referenceClass = UserProfile.class)
	private UserRef createdBy;
	
	@Reference(referenceClass = UserProfile.class)
	private UserRef lastUpdatedBy;

	@Reference(referenceClass = Organization.class)
	private Organization owner;
	
	private String name;
	
	private ConnectorType connectorType;
	
	private String status;
	
	private Date connectedOn;
	
	private Boolean isConnected;
	
	private String connectedAs;
	
	private String username;
	
	private String password;
	
	private String clientId;
	
	private String clientSecret;
	
	@EmbedOne
	private SalesforceMetadata salesforceMetadata;
	
	public Connector() {
		
	}

	public UserRef getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(UserRef createdBy) {
		this.createdBy = createdBy;
	}

	public UserRef getLastUpdatedBy() {
		return lastUpdatedBy;
	}

	public void setLastUpdatedBy(UserRef lastUpdatedBy) {
		this.lastUpdatedBy = lastUpdatedBy;
	}

	public Organization getOwner() {
		return owner;
	}

	public void setOwner(Organization owner) {
		this.owner = owner;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ConnectorType getConnectorType() {
		return connectorType;
	}
	
	public void setConnectorType(ConnectorType connectorType) {
		this.connectorType = connectorType;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Date getConnectedOn() {
		return connectedOn;
	}

	public void setConnectedOn(Date connectedOn) {
		this.connectedOn = connectedOn;
	}

	public Boolean getIsConnected() {
		return isConnected;
	}

	public void setIsConnected(Boolean isConnected) {
		this.isConnected = isConnected;
	}

	public String getConnectedAs() {
		return connectedAs;
	}

	public void setConnectedAs(String connectedAs) {
		this.connectedAs = connectedAs;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getClientSecret() {
		return clientSecret;
	}

	public void setClientSecret(String clientSecret) {
		this.clientSecret = clientSecret;
	}

	public SalesforceMetadata getSalesforceMetadata() {
		return salesforceMetadata;
	}

	public void setSalesforceMetadata(SalesforceMetadata salesforceMetadata) {
		this.salesforceMetadata = salesforceMetadata;
	}
}