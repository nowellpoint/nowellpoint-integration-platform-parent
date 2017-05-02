package com.nowellpoint.slack;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Payload {
	
	private String text;
	
	private String username;
	
	@JsonProperty(value="icon_url")
	private String iconUrl;
	
	public Payload() {
		
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getIconUrl() {
		return iconUrl;
	}

	public void setIconUrl(String iconUrl) {
		this.iconUrl = iconUrl;
	}
}