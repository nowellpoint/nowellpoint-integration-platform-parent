package com.nowellpoint.api.model.document;

import java.io.Serializable;

public class Source implements Serializable {

	private static final long serialVersionUID = -5595654748088368069L;

	private String type;
	
	private String id;
	
	private String connectionString;
	
	public Source() {
		
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