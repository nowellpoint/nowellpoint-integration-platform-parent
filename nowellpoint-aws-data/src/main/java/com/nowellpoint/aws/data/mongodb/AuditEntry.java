package com.nowellpoint.aws.data.mongodb;

import java.util.Date;

import org.joda.time.Instant;

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
	
	private Object object;
	
	public AuditEntry() {
		Date now = Instant.now().toDate();
		setCreatedDate(now);
		setLastModifiedDate(now);
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

	public Object getObject() {
		return object;
	}

	public void setObject(Object object) {
		this.object = object;
	}
}