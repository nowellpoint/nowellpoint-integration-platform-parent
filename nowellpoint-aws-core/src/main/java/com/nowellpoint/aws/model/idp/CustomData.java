package com.nowellpoint.aws.model.idp;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CustomData implements Serializable {

	/**
	 * 
	 */
	
	private static final long serialVersionUID = 5897243853246024323L;
	
	@JsonProperty(value="href")
	private String href;
	
	@JsonProperty(value="createdAt")
	private Date createdAt;
	
	@JsonProperty(value="modifiedAt")
	private Date modifiedAt;
	
	@JsonProperty(value="applicationUserId")
	private String applicationUserId;
	
	@JsonProperty(value="mongodbConnectUri")
	private String mongodbConnectUri;

	public CustomData() {
		
	}

	public String getHref() {
		return href;
	}

	public void setHref(String href) {
		this.href = href;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	public Date getModifiedAt() {
		return modifiedAt;
	}

	public void setModifiedAt(Date modifiedAt) {
		this.modifiedAt = modifiedAt;
	}

	public String getApplicationUserId() {
		return applicationUserId;
	}

	public void setApplicationUserId(String applicationUserId) {
		this.applicationUserId = applicationUserId;
	}

	public String getMongodbConnectUri() {
		return mongodbConnectUri;
	}

	public void setMongodbConnectUri(String mongodbConnectUri) {
		this.mongodbConnectUri = mongodbConnectUri;
	}
}