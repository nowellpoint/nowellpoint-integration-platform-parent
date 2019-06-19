package com.nowellpoint.client.sforce.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.nowellpoint.client.sforce.annotation.Column;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserInfo implements Serializable {

	private static final long serialVersionUID = 3163086585922281575L;
	
	@Getter @JsonProperty("attributes") private Attributes attributes;
	@Getter @Column(name="Id") @JsonProperty("Id") private String id;
	@Getter @Column(name="Username") @JsonProperty("Username") private String username;
	@Getter @Column(name="LastName") @JsonProperty("LastName") private String lastName;
	@Getter @Column(name="FirstName") @JsonProperty("FirstName") private String firstName;
	@Getter @Column(name="Name") @JsonProperty("Name") private String name;
	@Getter @Column(name="Email") @JsonProperty("Email") private String email;
	@Getter @Column(name="IsActive") @JsonProperty("IsActive") private Boolean isActive;
}