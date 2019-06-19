package com.nowellpoint.client.sforce.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
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
	
	@Getter @JsonProperty("Name") private String name;
	@Getter @JsonProperty("DeveloperName") private String developerName;
}