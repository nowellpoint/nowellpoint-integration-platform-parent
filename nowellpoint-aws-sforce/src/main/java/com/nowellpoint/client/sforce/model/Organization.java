package com.nowellpoint.client.sforce.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Organization extends SObject {
	
	private static final long serialVersionUID = -1315510190045597737L;
	
	public static final String QUERY = "Select "
			+ "Address, "
			+ "ComplianceBccEmail, "
			+ "CreatedById, "
			+ "CreatedDate, "
			+ "DefaultAccountAccess, "
			+ "DefaultCalendarAccess, "
			+ "DefaultCampaignAccess, "
			+ "DefaultCaseAccess, "
			+ "DefaultContactAccess, "
			+ "DefaultLeadAccess, "
			+ "DefaultLocaleSidKey, "
			+ "DefaultOpportunityAccess, "
			+ "DefaultPricebookAccess, "
			+ "Division, "
			+ "Fax, "
			+ "FiscalYearStartMonth, "
			+ "GeocodeAccuracy, "
			+ "Id, "
			+ "InstanceName, "
			+ "IsReadOnly, "
			+ "IsSandbox, "
			+ "LanguageLocaleKey, "
			+ "LastModifiedById, "
			+ "LastModifiedDate, "
			+ "MonthlyPageViewsEntitlement,"
			+ "MonthlyPageViewsUsed, "
			+ "Name, "
			+ "NamespacePrefix, "
			+ "NumKnowledgeService, "
			+ "OrganizationType, "
			+ "Phone, "
			+ "PreferencesConsentManagementEnabled, "
			+ "PreferencesIndividualAutoCreateEnabled, "
			+ "PreferencesLightningLoginEnabled, "
			+ "PreferencesOnlyLLPermUserAllowed, "
			+ "PreferencesRequireOpportunityProducts, "
			+ "PreferencesTerminateOldestSession, "
			+ "PreferencesTransactionSecurityPolicy, "
			+ "PrimaryContact, "
			+ "ReceivesAdminInfoEmails, "
			+ "ReceivesInfoEmails, "
			+ "SignupCountryIsoCode, "
			+ "TrialExpirationDate, "
			+ "UiSkin, "
			+ "UsesStartDateAsFiscalYearName, "
			+ "WebToCaseDefaultOrigin "
			+ "From Organization";
	
	@Getter @JsonProperty(value="Division") private String division;
	@Getter @JsonProperty(value="Fax") private String fax;
	@Getter @JsonProperty(value="DefaultLocaleSidKey") private String defaultLocaleSidKey;
	@Getter @JsonProperty(value="FiscalYearStartMonth") private String fiscalYearStartMonth;
	@Getter @JsonProperty(value="InstanceName") private String instanceName;
	@Getter @JsonProperty(value="IsSandbox") private Boolean isSandbox;
	@Getter @JsonProperty(value="LanguageLocaleKey") private String languageLocaleKey;
	@Getter @JsonProperty(value="Name") private String name;
	@Getter @JsonProperty(value="OrganizationType") private String organizationType;
	@Getter @JsonProperty(value="Phone") private String phone;
	@Getter @JsonProperty(value="PrimaryContact") private String primaryContact;
	@Getter @JsonProperty(value="UsesStartDateAsFiscalYearName") private Boolean usesStartDateAsFiscalYearName;
	@Getter @JsonProperty(value="Address") private Address address;
}