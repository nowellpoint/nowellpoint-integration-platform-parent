package com.nowellpoint.aws.idp.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
public class Account implements Serializable {

	/**
	 * 
	 */
	
	private static final long serialVersionUID = 4359339056490011294L;
	
	private String id;
	
	@JsonProperty(value="href")
	private String href;
	
	@JsonProperty(value="username")
	private String username;
	
	@JsonProperty(value="email")
	private String email;
	
	@JsonProperty(value="fullName")
	private String fullName;
	
	@JsonProperty(value="givenName")
	private String givenName;
	
	@JsonProperty(value="middleName")
	private String middleName;
	
	@JsonProperty(value="surname")
	private String surname;
	
	@JsonProperty(value="status")
	private String status;
	
	@JsonProperty(value="password")
	private String password;
	
	@JsonProperty(value="emailVerificationToken")
	private EmailVerificationToken emailVerificationToken;
	
	@JsonProperty(value="customData")
	private CustomData customData;
	
	@JsonProperty(value="tenant")
	private Tenant tenant;
	
	@JsonProperty(value="groups")
	private Groups groups;
	
	@JsonProperty(value="groupMemberships")
	private GroupMemberships groupMemberships;
	
	@JsonProperty(value="directory")
	private Directory directory;
	  
	public Account() {
		
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getHref() {
		return href;
	}

	public void setHref(String href) {
		setId(href.substring(href.lastIndexOf("/") + 1));
		this.href = href;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getGivenName() {
		return givenName;
	}

	public void setGivenName(String givenName) {
		this.givenName = givenName;
	}

	public String getMiddleName() {
		return middleName;
	}

	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}

	public String getSurname() {
		return surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getPassword() {
		return password;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	
	public CustomData getCustomData() {
		return customData;
	}

	public void setCustomData(CustomData customData) {
		this.customData = customData;
	}

	public Tenant getTenant() {
		return tenant;
	}

	public void setTenant(Tenant tenant) {
		this.tenant = tenant;
	}

	public EmailVerificationToken getEmailVerificationToken() {
		return emailVerificationToken;
	}

	public void setEmailVerificationToken(EmailVerificationToken emailVerificationToken) {
		this.emailVerificationToken = emailVerificationToken;
	}

	public Groups getGroups() {
		return groups;
	}

	public void setGroups(Groups groups) {
		this.groups = groups;
	}

	public GroupMemberships getGroupMemberships() {
		return groupMemberships;
	}

	public void setGroupMemberships(GroupMemberships groupMemberships) {
		this.groupMemberships = groupMemberships;
	}

	public Directory getDirectory() {
		return directory;
	}

	public void setDirectory(Directory directory) {
		this.directory = directory;
	}
}