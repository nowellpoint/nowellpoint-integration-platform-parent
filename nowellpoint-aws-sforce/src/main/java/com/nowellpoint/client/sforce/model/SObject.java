package com.nowellpoint.client.sforce.model;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.nowellpoint.client.sforce.annotation.Column;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class SObject implements Serializable {
	
	private static final long serialVersionUID = 8436267729392469449L;
	
	protected static final String SOBJECT_QUERY = "Select "
			+ "Id, "
			+ "Name, "
			+ "CreatedBy.Name, "
			+ "CreatedBy.Id, "
			+ "CreatedBy.FirstName, "
			+ "CreatedBy.LastName, "
			+ "CreatedBy.Email, "
			+ "CreatedBy.Username, "
			+ "CreatedDate, "
			+ "LastModifiedBy.Name, "
			+ "LastModifiedBy.Id, "
			+ "LastModifiedBy.FirstName, "
			+ "LastModifiedBy.LastName, "
			+ "LastModifiedBy.Email, "
			+ "LastModifiedBy.Username, "
			+ "LastModifiedDate ";

	@Getter @JsonProperty("attributes") private Attributes attributes;
	@Getter @Column(name="Id") @JsonProperty("Id") private String id;
	@Getter @Column(name="Name") @JsonProperty("Name") private String name;
	@Getter @Column(name="CreatedDate") @JsonProperty("CreatedDate") private Date createdDate;
	@Getter @Column(name="LastModifiedDate") @JsonProperty("LastModifiedDate") private Date lastModifiedDate;
	@Getter @Column(name="CreatedBy") @JsonProperty(value="CreatedBy") private UserInfo createdBy;
	@Getter @Column(name="LastModifiedBy") @JsonProperty(value="LastModifiedBy") private UserInfo lastModifiedBy;
}