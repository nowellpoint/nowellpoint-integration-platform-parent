package com.nowellpoint.aws.model.data;

import java.io.Serializable;

public class UserContext implements Serializable {
	
	/**
	 * 
	 */
	
	private static final long serialVersionUID = -4836277683837614857L;

	private String mongodbConnectUri;
	
	private String userId;
	
	public UserContext() {
		
	}

	public String getMongoDBConnectUri() {
		return mongodbConnectUri;
	}

	public void setMongoDBConnectUri(String mongodbConnectUri) {
		this.mongodbConnectUri = mongodbConnectUri;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}
	
	public UserContext withMongoDBConnectUri(String mongodbConnectUri) {
		setMongoDBConnectUri(mongodbConnectUri);
		return this;
	}

	public UserContext withUserId(String userId) {
		setUserId(userId);
		return this;
	}
}
