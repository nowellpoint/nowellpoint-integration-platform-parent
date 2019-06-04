package com.nowellpoint.client.sforce.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UserInfo implements Serializable {

	private static final long serialVersionUID = 3163086585922281575L;
	
	@Getter @JsonProperty("attributes") private Attributes attributes;
	@Getter @JsonProperty("Id") private String id;
	@Getter @JsonProperty("Username") private String username;
	@Getter @JsonProperty("LastName") private String lastName;
	@Getter @JsonProperty("FirstName") private String firstName;
	@Getter @JsonProperty("Name") private String name;
	@Getter @JsonProperty("Email") private String email;
	@Getter @JsonProperty("IsActive") private Boolean isActive;

	public UserInfo() {

	}
}