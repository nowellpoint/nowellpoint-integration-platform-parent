package com.nowellpoint.client.sforce.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;

@JsonIgnoreProperties(ignoreUnknown = true)
public class User extends SObject {

	private static final long serialVersionUID = 3163086585922281575L;
	
	@Getter @JsonProperty("AboutMe") private String aboutMe;
	@Getter @JsonProperty("Username") private String username;
	@Getter @JsonProperty("LastName") private String lastName;
	@Getter @JsonProperty("FirstName") private String firstName;
	@Getter @JsonProperty("Name") private String name;
	@Getter @JsonProperty("Company") private String company;
	@Getter @JsonProperty("Division") private String division;
	@Getter @JsonProperty("Department") private String department;
	@Getter @JsonProperty("Title") private String title;
	@Getter @JsonProperty("Street") private String street;
	@Getter @JsonProperty("City") private String city;
	@Getter @JsonProperty("State") private String state;
	@Getter @JsonProperty("PostalCode") private String postalCode;
	@Getter @JsonProperty("Country") private String country;
	@Getter @JsonProperty("Latitude") private String latitude;
	@Getter @JsonProperty("Longitude") private String longitude;
	@Getter @JsonProperty("Email") private String email;
	@Getter @JsonProperty("SenderEmail") private String senderEmail;
	@Getter @JsonProperty("SenderName") private String senderName;
	@Getter @JsonProperty("Signature") private String signature;
	@Getter @JsonProperty("Phone") private String phone;
	@Getter @JsonProperty("Extension") private String extension;
	@Getter @JsonProperty("Fax") private String fax;
	@Getter @JsonProperty("MobilePhone") private String mobilePhone;
	@Getter @JsonProperty("Alias") private String alias;
	@Getter @JsonProperty("CommunityNickname") private String communityNickname;
	@Getter @JsonProperty("IsActive") private Boolean isActive;
	@Getter @JsonProperty("TimeZoneSidKey") private String timeZoneSidKey;
	@Getter @JsonProperty("LocaleSidKey") private String localeSidKey;
	@Getter @JsonProperty("EmailEncodingKey") private String emailEncodingKey;
	@Getter @JsonProperty("PermissionsCustomizeApplication") private Boolean permissionsCustomizeApplication;
	@Getter @JsonProperty("UserType") private String userType;
	@Getter @JsonProperty("LanguageLocaleKey") private String languageLocaleKey;
	@Getter @JsonProperty("EmployeeNumber") private String employeeNumber;

	public User() {

	}
}