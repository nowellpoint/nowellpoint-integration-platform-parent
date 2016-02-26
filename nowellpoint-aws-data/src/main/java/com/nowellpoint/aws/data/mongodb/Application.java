package com.nowellpoint.aws.data.mongodb;

import java.io.Serializable;

import org.bson.types.ObjectId;

import com.nowellpoint.aws.api.bus.ApplicationMessageListener;
import com.nowellpoint.aws.data.ApplicationCodec;
import com.nowellpoint.aws.data.annotation.MessageHandler;

@MessageHandler(queueName="MONGODB_APPLICATION_COLLECTION_QUEUE", collectionName="applications", messageListener=ApplicationMessageListener.class, codec=ApplicationCodec.class)
public class Application extends AbstractDocument implements Serializable {
	
	/**
	 * 
	 */
	
	private static final long serialVersionUID = -1315510190045597737L;
	
	private Boolean isSandbox;
	
	private String name;
	
	private String type;
	
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
}