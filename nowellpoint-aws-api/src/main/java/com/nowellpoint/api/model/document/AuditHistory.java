package com.nowellpoint.api.model.document;

import java.util.Date;

import org.bson.types.ObjectId;
import org.joda.time.Instant;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.nowellpoint.api.model.codec.AuditHistoryCodec;
import com.nowellpoint.mongodb.annotation.Document;
import com.nowellpoint.mongodb.document.MongoDocument;
import com.nowellpoint.mongodb.document.ObjectIdDeserializer;
import com.nowellpoint.mongodb.document.ObjectIdSerializer;

@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Document(collectionName="audit.history", codec=AuditHistoryCodec.class)
public class AuditHistory extends MongoDocument {

	/**
	 * 
	 */

	private static final long serialVersionUID = 3163086585922281575L;
	
	public enum Event { 
			INSERT,
			UPDATE,
			DELETE
	}
	
	@JsonSerialize(using = ObjectIdSerializer.class)
	@JsonDeserialize(using = ObjectIdDeserializer.class)
	private ObjectId sourceId;
	
	private String event;
	
	private String type;
	
	private MongoDocument document;

	public AuditHistory() {
		Date now = Instant.now().toDate();
		setCreatedDate(now);
		setLastModifiedDate(now);
	}

	public ObjectId getSourceId() {
		return sourceId;
	}

	public void setSourceId(ObjectId sourceId) {
		this.sourceId = sourceId;
	}

	public String getEvent() {
		return event;
	}

	public void setEvent(String event) {
		this.event = event;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public MongoDocument getDocument() {
		return document;
	}

	public void setDocument(MongoDocument document) {
		this.document = document;
	}
	
	public AuditHistory withSourceId(ObjectId sourceId) {
		setSourceId(sourceId);
		return this;
	}
	
	public AuditHistory withCreatedById(String createdById) {
		setCreatedById(createdById);
		return this;
	}
	
	public AuditHistory withDocument(MongoDocument document) {
		setDocument(document);
		setType(document.getClass().getName());
		return this;
	}
	
	public AuditHistory withEvent(Event event) {
		setEvent(event.name());
		return this;
	}
	
	public AuditHistory withLastModifiedById(String lastModifiedById) {
		setLastModifiedById(lastModifiedById);
		return this;
	}
}