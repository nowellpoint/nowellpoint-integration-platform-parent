package com.nowellpoint.aws.data.mongodb;

import java.io.Serializable;

import org.bson.types.ObjectId;

import com.nowellpoint.aws.data.ApplicationCodec;
import com.nowellpoint.aws.data.annotation.Document;

@Document(collectionName="applications", codec=ApplicationCodec.class)
public class Application extends AbstractDocument implements Serializable {
	
	/**
	 * 
	 */
	
	private static final long serialVersionUID = -1315510190045597737L;
	
	private Boolean isSandbox;
	
	private String name;
	
	private String type;
	
	private String key;
	
	private String instanceName;
	
	private String url;
	
	public Application() {
		
	}
	
	public Application(ObjectId id) {
		setId(id);
	}

	public Boolean getIsSandbox() {
		return isSandbox;
	}

	public void setIsSandbox(Boolean isSandbox) {
		this.isSandbox = isSandbox;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getInstanceName() {
		return instanceName;
	}

	public void setInstanceName(String instanceName) {
		this.instanceName = instanceName;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
}