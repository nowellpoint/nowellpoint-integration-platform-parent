package com.nowellpoint.client.sforce.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Account extends SObject {
	
	private static final long serialVersionUID = 1402870861788633127L;
	
	public static final String QUERY = "Select "
			+ "Id, "
			+ "Name, "
			+ "CreatedById, "
			+ "CreatedDate, "
			+ "LastModifiedById, "
			+ "LastModifiedDate "
			+ "From Account ";

	public Account() { }
}