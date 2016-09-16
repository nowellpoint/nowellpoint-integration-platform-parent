package com.nowellpoint.api.model.document;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.mongodb.DBRef;

@JsonIgnoreProperties(ignoreUnknown = true)
public class User implements Serializable {

	/**
	 * 
	 */
	
	private static final long serialVersionUID = 5065948775547812678L;
	
	@JsonSerialize(using=DBRefSerializer.class)
	@JsonDeserialize(using=DBRefDeserializer.class)
	private DBRef identity;
	
	public User() {
		
	}

	public DBRef getIdentity() {
		return identity;
	}

	public void setIdentity(DBRef identity) {
		this.identity = identity;
	}
}