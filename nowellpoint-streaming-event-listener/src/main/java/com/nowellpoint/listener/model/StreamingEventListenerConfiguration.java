package com.nowellpoint.listener.model;

import java.io.Serializable;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

@Entity(value = "streaming.event.listener.configurations")
public class StreamingEventListenerConfiguration implements Serializable {
	
	private static final long serialVersionUID = -1074167755763471323L;
	
	@Id
	private ObjectId id;
	
	private ObjectId organizationId;
	
	private String topicId;
	
	private String apiVersion;
	
	private String source;
	
	private String channel;
	
	private Long replayId;
	
	private String refreshToken;

	public StreamingEventListenerConfiguration() {
		
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

	public Long getReplayId() {
		return replayId;
	}

	public void setReplayId(Long replayId) {
		this.replayId = replayId;
	}

	public String getRefreshToken() {
		return refreshToken;
	}

	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}
}