package com.nowellpoint.client.sforce.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UserRole extends SObject {
	
	private static final long serialVersionUID = -1999951017662749599L;

	public static final String QUERY = "Select "
			+ "CaseAccessForAccountOwner, "
			+ "ContactAccessForAccountOwner, "
			+ "DeveloperName, "
			+ "ForecastUserId, "
			+ "Id, "
			+ "LastModifiedById, "
			+ "LastModifiedDate, "
			+ "MayForecastManagerShare, "
			+ "Name, "
			+ "OpportunityAccessForAccountOwner, "
			+ "ParentRoleId, "
			+ "PortalAccountId, "
			+ "PortalAccountOwnerId, "
			+ "PortalType, "
			+ "RollupDescription "
			+ "From UserRole";
	
	@JsonProperty("Name")
	private String name;
	
	@JsonProperty("DeveloperName")
	private String developerName;
	
	public UserRole() {
		
	}

	public String getName() {
		return name;
	}

	public String getDeveloperName() {
		return developerName;
	}
}