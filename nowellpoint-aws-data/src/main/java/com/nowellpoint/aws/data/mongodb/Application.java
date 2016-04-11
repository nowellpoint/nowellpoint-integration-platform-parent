package com.nowellpoint.aws.data.mongodb;

import java.io.Serializable;

import org.bson.types.ObjectId;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.nowellpoint.aws.data.ApplicationCodec;
import com.nowellpoint.aws.data.annotation.Document;

@JsonIgnoreProperties(ignoreUnknown = true)
@Document(collectionName="applications", codec=ApplicationCodec.class)
public class Application extends AbstractDocument implements Serializable {
	
	/**
	 * 
	 */
	
	private static final long serialVersionUID = -1315510190045597737L;
	
	private String name;
	
	private ServiceInstance serviceInstance;
	
	private String sourceId;
	
	private String sourceName;
	
	
	public Application() {
		
	}
	
	public Application(ObjectId id) {
		setId(id);
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public ServiceInstance getServiceInstance() {
		return serviceInstance;
	}

	public void setServiceInstance(ServiceInstance serviceInstance) {
		this.serviceInstance = serviceInstance;
	}

	public String getSourceId() {
		return sourceId;
	}

	public void setSourceId(String sourceId) {
		this.sourceId = sourceId;
	}

	public String getSourceName() {
		return sourceName;
	}
	
	public void setSourceName(String sourceName) {
		this.sourceName = sourceName;
	}
}