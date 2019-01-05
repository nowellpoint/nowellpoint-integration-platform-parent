package com.nowellpoint.console.entity;

import java.io.Serializable;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Field;
import org.mongodb.morphia.annotations.Index;
import org.mongodb.morphia.annotations.IndexOptions;
import org.mongodb.morphia.annotations.Indexes;

@Entity(value = "streaming.event.listener.configurations")
@Indexes(
		@Index(fields = { @Field("topicId") }, options = @IndexOptions(unique = true))
)
public class StreamingEventListenerConfiguration extends BaseEntity implements Serializable {
	
	private static final long serialVersionUID = -1074167755763471323L;
	
	private ObjectId organizationId;
	
	private Boolean active;
	
	private String topicId;
	
	private String apiVersion;
	
	private String source;
	
	private String channel;
	
	private String refreshToken;

	public StreamingEventListenerConfiguration() {
		
	}
	
	public void setOrganizationId(String organizationId) {
		this.organizationId = new ObjectId(organizationId);
	}

	public ObjectId getOrganizationId() {
		return organizationId;
	}

	public void setOrganizationId(ObjectId organizationId) {
		this.organizationId = organizationId;
	}

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	public String getTopicId() {
		return topicId;
	}

	public void setTopicId(String topicId) {
		this.topicId = topicId;
	}

	public String getApiVersion() {
		return apiVersion;
	}

	public void setApiVersion(String apiVersion) {
		this.apiVersion = apiVersion;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getChannel() {
		return channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}

	public String getRefreshToken() {
		return refreshToken;
	}

	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}
}