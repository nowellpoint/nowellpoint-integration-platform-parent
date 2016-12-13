package com.nowellpoint.client.sforce;

import java.util.Date;

public class DescribeSobjectRequest {
	
	private String sobjectsUrl;
	
	private String accessToken;
	
	private String sobject;
	
	private Date ifModifiedSince;
	
	public DescribeSobjectRequest() {
		
	}

	public String getSobjectsUrl() {
		return sobjectsUrl;
	}

	public DescribeSobjectRequest withSobjectsUrl(String sobjectsUrl) {
		this.sobjectsUrl = sobjectsUrl;
		return this;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public Date getIfModifiedSince() {
		return ifModifiedSince;
	}

	public void setIfModifiedSince(Date ifModifiedSince) {
		this.ifModifiedSince = ifModifiedSince;
	}

	public DescribeSobjectRequest withAccessToken(String accessToken) {
		this.accessToken = accessToken;
		return this;
	}

	public String getSobject() {
		return sobject;
	}

	public DescribeSobjectRequest withSobject(String sobject) {
		this.sobject = sobject;
		return this;
	}
	
	public DescribeSobjectRequest withIfModifiedSince(Date ifModifiedSince) {
		this.ifModifiedSince = ifModifiedSince;
		return this;
	}
}