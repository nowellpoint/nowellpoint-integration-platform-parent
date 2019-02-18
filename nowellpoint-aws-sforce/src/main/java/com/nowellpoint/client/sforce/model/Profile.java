package com.nowellpoint.client.sforce.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Profile extends SObject {
	
	private static final long serialVersionUID = 3004892024883616890L;

	public static final String QUERY = "Select "
			+ "CreatedById, "
			+ "CreatedDate, "
			+ "Description, "
			+ "Id, "
			+ "LastModifiedById, "
			+ "LastModifiedDate, "
			+ "LastReferencedDate, "
			+ "LastViewedDate, "
			+ "Name, "
			+ "UserLicenseId, "
			+ "UserType "
			+ "From Profile ";
	
	@JsonProperty("Name")
	private String name;
	
	@JsonProperty("Description")
	private String description;
	
	@JsonProperty("UserType")
	private String userType;
	
	public Profile() {
		
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public String getUserType() {
		return userType;
	}

	public void setUserType(String userType) {
		this.userType = userType;
	}
}