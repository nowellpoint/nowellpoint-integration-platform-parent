package com.nowellpoint.listener.model;

import java.io.Serializable;
import java.util.Date;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Field;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Index;
import org.mongodb.morphia.annotations.IndexOptions;
import org.mongodb.morphia.annotations.Indexes;

@Entity(value = "streaming.events")
@Indexes(
		@Index(fields = { @Field("replayId"), @Field("organizationId"), @Field("source") }, options = @IndexOptions(unique = true))
)
public class StreamingEvent implements Serializable {

	private static final long serialVersionUID = -226649098013040674L;
	
	@Id
	private ObjectId id;
	
	private Date eventDate;
	
	private ObjectId organizationId;
	
	private Long replayId;
	
	private String type;
	
	private String source;
	
	private Payload payload;
	
	public StreamingEvent() {
		
	}

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public ObjectId getOrganizationId() {
		return organizationId;
	}

	public void setOrganizationId(ObjectId organizationId) {
		this.organizationId = organizationId;
	}

	public Date getEventDate() {
		return eventDate;
	}

	public void setEventDate(Date eventDate) {
		this.eventDate = eventDate;
	}

	public Long getReplayId() {
		return replayId;
	}

	public void setReplayId(Long replayId) {
		this.replayId = replayId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public Payload getPayload() {
		return payload;
	}

	public void setPayload(Payload payload) {
		this.payload = payload;
	}
}