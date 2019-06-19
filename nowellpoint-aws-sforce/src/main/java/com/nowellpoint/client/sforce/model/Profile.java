package com.nowellpoint.client.sforce.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
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
	
	@Getter @JsonProperty("Name") private String name;
	@Getter @JsonProperty("Description") private String description;
	@Getter @JsonProperty("UserType") private String userType;
}