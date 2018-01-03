package com.nowellpoint.api.model.document;

import java.util.Date;

import com.nowellpoint.mongodb.annotation.Document;
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
	
	private String type;
	
	private String typeName;
	
	private String authEndpoint;
	
	private String iconHref;
	
	private String grantType;
	
	private String status;
	
	private Date connectedOn;
	
	private Boolean isConnected;
	
	private String connectedAs;
	
	private String username;
	
	private String clientId;
	
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

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getTypeName() {
		return typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

	public String getAuthEndpoint() {
		return authEndpoint;
	}

	public void setAuthEndpoint(String authEndpoint) {
		this.authEndpoint = authEndpoint;
	}

	public String getIconHref() {
		return iconHref;
	}

	public void setIconHref(String iconHref) {
		this.iconHref = iconHref;
	}

	public String getGrantType() {
		return grantType;
	}

	public void setGrantType(String grantType) {
		this.grantType = grantType;
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
}