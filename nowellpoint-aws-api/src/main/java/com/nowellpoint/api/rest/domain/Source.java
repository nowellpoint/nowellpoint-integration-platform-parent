package com.nowellpoint.api.rest.domain;

public class Source {

	private String type;
	
	private String id;
	
	private String connectionString;
	
	private Source() {
		
	}
	
	private Source(String type, String id, String connectionString) {
		this.type = type;
		this.id = id;
		this.connectionString = connectionString;
	}
	
	public static Source of(SalesforceConnector connector) {
		return new Source(
				Types.SALESFORCE, 
				connector.getId(), 
				connector.getConnectionString());
	}
	
	public class Types {
		public static final String SALESFORCE = "SALESFORCE";
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getConnectionString() {
		return connectionString;
	}

	public void setConnectionString(String connectionString) {
		this.connectionString = connectionString;
	}
}