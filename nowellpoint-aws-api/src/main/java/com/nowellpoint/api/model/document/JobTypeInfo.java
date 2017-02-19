package com.nowellpoint.api.model.document;

import java.io.Serializable;

import org.bson.types.ObjectId;

import com.nowellpoint.mongodb.annotation.EmbedOne;

public class JobTypeInfo implements Serializable {
	
	private static final long serialVersionUID = -715880262310009511L;

	private ObjectId id;
	
	private String name;
	
	private String code;
	
	private String description;
	
	@EmbedOne
	private ConnectorType connectorType;
	
	public JobTypeInfo() {
		
	}

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public ConnectorType getConnectorType() {
		return connectorType;
	}

	public void setConnectorType(ConnectorType connectorType) {
		this.connectorType = connectorType;
	}
}