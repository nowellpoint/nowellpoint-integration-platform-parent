package com.nowellpoint.client.sforce;

public class CountRequest {
	
	private String accessToken;

	private String queryUrl;
	
	private String sobject;
	
	private String whereClause;
	
	public CountRequest() {
		
	}

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public String getQueryUrl() {
		return queryUrl;
	}
	
	public void setQueryUrl(String queryUrl) {
		this.queryUrl = queryUrl;
	}

	public String getQueryString() {
		String queryString = "Select Count(Id) From ".concat(getSobject());
		if (getWhereClause() != null && ! getWhereClause().trim().isEmpty()) {
			queryString.concat(getWhereClause());
		}
		return queryString;
	}
	
	public String getSobject() {
		return sobject;
	}

	public void setSobject(String sobject) {
		this.sobject = sobject;
	}

	public String getWhereClause() {
		return whereClause;
	}

	public void setWhereClause(String whereClause) {
		this.whereClause = whereClause;
	}

	public CountRequest withAccessToken(String accessToken) {
		setAccessToken(accessToken);
		return this;
	}
	
	public CountRequest withQueryUrl(String queryUrl) {
		setQueryUrl(queryUrl);
		return this;
	}
	
	public CountRequest withSobject(String sobject) {
		setSobject(sobject);
		return this;
	}
	
	public CountRequest withWhereClause(String whereClause) {
		setWhereClause(whereClause);
		return this;
	}
}