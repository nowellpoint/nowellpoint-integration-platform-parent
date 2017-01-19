package com.nowellpoint.mongodb.document;

import java.io.Serializable;
import java.util.Date;

import org.bson.types.ObjectId;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.nowellpoint.mongodb.annotation.Id;
import com.nowellpoint.mongodb.annotation.MappedSuperclass;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@MappedSuperclass
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class MongoDocument implements Serializable {
	
	/**
	 * 
	 */
	
	private static final long serialVersionUID = 4165321769330346404L;
	
	/**
	 * 
	 */
	
	@Id
	@JsonProperty("_id")
	@JsonSerialize(using = ObjectIdSerializer.class)
	@JsonDeserialize(using = ObjectIdDeserializer.class)
	private ObjectId id;
	
	/**
	 * 
	 */
	
	@JsonSerialize(using = DateSerializer.class)
	@JsonDeserialize(using = DateDeserializer.class)
	@JsonInclude(Include.NON_NULL)
	private Date createdDate;
	
	/**
	 * 
	 */
	
	@JsonSerialize(using = DateSerializer.class)
	@JsonDeserialize(using = DateDeserializer.class)
	@JsonInclude(Include.NON_NULL)
	private Date lastModifiedDate;
	
	/**
	 * 
	 */
	
	@JsonSerialize(using = DateSerializer.class)
	@JsonDeserialize(using = DateDeserializer.class)
	@JsonInclude(Include.NON_NULL)
	private Date systemCreatedDate;
	
	/**
	 * 
	 */

	@JsonSerialize(using = DateSerializer.class)
	@JsonDeserialize(using = DateDeserializer.class)
	@JsonInclude(Include.NON_NULL)
	private Date systemModifiedDate;
	
	public MongoDocument() {
		
	}
	
	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public Date getLastModifiedDate() {
		return lastModifiedDate;
	}

	public void setLastModifiedDate(Date lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}

	public Date getSystemCreatedDate() {
		return systemCreatedDate;
	}

	public void setSystemCreatedDate(Date systemCreatedDate) {
		this.systemCreatedDate = systemCreatedDate;
	}

	public Date getSystemModifiedDate() {
		return systemModifiedDate;
	}

	public void setSystemModifiedDate(Date systemModifiedDate) {
		this.systemModifiedDate = systemModifiedDate;
	}
}