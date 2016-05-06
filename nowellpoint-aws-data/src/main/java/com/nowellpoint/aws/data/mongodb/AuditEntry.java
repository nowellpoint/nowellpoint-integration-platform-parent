package com.nowellpoint.aws.data.mongodb;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.nowellpoint.aws.data.AuditEntryCodec;
import com.nowellpoint.aws.data.annotation.Document;

@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Document(collectionName="audit.entry", codec=AuditEntryCodec.class)
public class AuditEntry extends AbstractDocument {

	/**
	 * 
	 */
	
	private static final long serialVersionUID = 9090729792097644602L;
	
	private String resource;
	
	private String action;
	
	public AuditEntry() {
		
	}

	public String getResource() {
		return resource;
	}

	public void setResource(String resource) {
		this.resource = resource;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}
}