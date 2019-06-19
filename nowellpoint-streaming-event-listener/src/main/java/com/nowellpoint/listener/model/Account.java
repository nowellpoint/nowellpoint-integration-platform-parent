package com.nowellpoint.listener.model;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonIgnore;
import org.bson.codecs.pojo.annotations.BsonProperty;

import lombok.Builder;
import lombok.Getter;

@Builder(toBuilder=true)
public class Account {
	@Getter @BsonId private  String id;
	@Getter private String accountNumber;
	@Getter private String accountSource;
	@Getter private Double annualRevenue;
	@Getter private Address billingAddress;
	@Getter private String createdById;
	@Getter private Date createdDate;
	@Getter private String description;
	@Getter private String name;
	@Getter private Integer numberOfEmployees;
	@Getter private String industry;
	@Getter private String lastModifiedById;
	@Getter private Date lastModifiedDate;
	@Getter private String organizationId;
	@Getter private String ownerId;
	@Getter private String ownership;
	@Getter private String phone;
	@Getter private String rating;
	@Getter private Address shippingAddress;
	@Getter private String sic;
	@Getter private String sicDesc;
	@Getter private String site;
	@Getter private String tickerSymbol;
	@Getter private String type;
	@Getter private String website;
	@Getter @Builder.Default private Boolean isDeleted = Boolean.FALSE;
	@Getter private List<AccountEvent> events;
	
	@BsonCreator
	public Account(@BsonId String id,
			@BsonProperty("accountNumber") String accountNumber,
			@BsonProperty("accountSource") String accountSource,
			@BsonProperty("annualRevenue") Double annualRevenue,
			@BsonProperty("billingAddress") Address billingAddress,
			@BsonProperty("createdById") String createdById,
			@BsonProperty("createdDate") Date createdDate,
			@BsonProperty("description") String description,
			@BsonProperty("name") String name,
			@BsonProperty("numberOfEmployees") Integer numberOfEmployees,
			@BsonProperty("industry") String industry,
			@BsonProperty("lastModifiedById") String lastModifiedById,
			@BsonProperty("lastModifiedDate") Date lastModifiedDate,
			@BsonProperty("organizationId") String organizationId,
			@BsonProperty("ownerId") String ownerId,
			@BsonProperty("ownership") String ownership,
			@BsonProperty("phone") String phone,
			@BsonProperty("rating") String rating,
			@BsonProperty("shippingAddress") Address shippingAddress,
			@BsonProperty("sic") String sic,
			@BsonProperty("sicDesc") String sicDesc,
			@BsonProperty("site") String site,
			@BsonProperty("tickerSymbol") String tickerSymbol,
			@BsonProperty("type") String type,
			@BsonProperty("website") String website,
			@BsonProperty("isDeleted") Boolean isDeleted,
			@BsonProperty("events") List<AccountEvent> events) {
		
		this.id = id;
		this.accountNumber = accountNumber;
		this.accountSource = accountSource;
		this.annualRevenue = annualRevenue;
		this.billingAddress = billingAddress;
		this.createdById = createdById;
		this.createdDate = createdDate;
		this.description = description;
		this.name = name;
		this.numberOfEmployees = numberOfEmployees;
		this.industry = industry;
		this.lastModifiedById = lastModifiedById;
		this.lastModifiedDate = lastModifiedDate;
		this.organizationId = organizationId;
		this.ownerId = ownerId;
		this.ownership = ownership;
		this.phone = phone;
		this.rating = rating;
		this.shippingAddress = shippingAddress;
		this.sic = sic;
		this.sicDesc = sicDesc;
		this.site = site;
		this.tickerSymbol = tickerSymbol;
		this.type = type;
		this.website = website;
		this.isDeleted = isDeleted;
		this.events = events;
	}
	
	@BsonIgnore
	public Map<String,Object> getAttributesAsMap() {
		Map<String,Object> attributes = new HashMap<String, Object>();
		attributes.put("AccountNumber", getAccountNumber());
		attributes.put("AccountSource", getAccountSource());
		attributes.put("AnnualRevenue", getAnnualRevenue());
		attributes.put("BillingAddress", getBillingAddress());
		attributes.put("CreatedById", getCreatedById());
		attributes.put("CreatedDate", getCreatedDate());
		attributes.put("Description", getDescription());
		attributes.put("Name", getName());
		attributes.put("NumberOfEmployees", getNumberOfEmployees());
		attributes.put("Industry", getIndustry());
		attributes.put("LastModifiedById", getLastModifiedById());
		attributes.put("LastModifiedDate", getLastModifiedDate());
		attributes.put("OwnerId", getOwnerId());
		attributes.put("Ownership", getOwnership());
		attributes.put("Phone", getPhone());
		attributes.put("Rating", getRating());
		attributes.put("ShippingAddress", getShippingAddress());
		attributes.put("Sic", getSic());
		attributes.put("SicDesc", getSicDesc());
		attributes.put("Site", getSite());
		attributes.put("TickerSymbol", getTickerSymbol());
		attributes.put("Type", getType());
		attributes.put("Website", getWebsite());
		return attributes;
	}
	
	public static Account of(String organizationId, com.nowellpoint.client.sforce.model.Account source) {
		Address billingAddress = Address.builder()
				.city(source.getBillingCity())
				.country(source.getBillingCountry())
				.countryCode(source.getBillingCountryCode())
				.latitude(source.getBillingLatitude())
				.longitude(source.getBillingLongitude())
				.postalCode(source.getBillingPostalCode())
				.state(source.getBillingState())
				.stateCode(source.getBillingStateCode())
				.street(source.getBillingStreet())
				.build();
		
		Address shippingAddress = Address.builder()
				.city(source.getShippingCity())
				.country(source.getShippingCountry())
				.countryCode(source.getShippingCountryCode())
				.latitude(source.getShippingLatitude())
				.longitude(source.getShippingLongitude())
				.postalCode(source.getShippingPostalCode())
				.state(source.getShippingState())
				.stateCode(source.getShippingStateCode())
				.street(source.getShippingStreet())
				.build();
		
		Account account = Account.builder()
				.accountNumber(source.getAccountNumber())
				.accountSource(source.getAccountSource())
				.annualRevenue(source.getAnnualRevenue())
				.billingAddress(billingAddress)
				.createdById(source.getCreatedBy().getId())
				.createdDate(source.getCreatedDate())
				.description(source.getDescription())
				.events(List.of())
				.id(source.getId())
				.industry(source.getIndustry())
				.lastModifiedById(source.getLastModifiedBy().getId())
				.lastModifiedDate(source.getLastModifiedDate())
				.name(source.getName())
				.numberOfEmployees(source.getNumberOfEmployees())
				.organizationId(organizationId)
				.ownerId(source.getOwner().getId())
				.ownership(source.getOwnership())
				.phone(source.getPhone())
				.rating(source.getRating())
				.shippingAddress(shippingAddress)
				.sic(source.getSic())
				.sicDesc(source.getSicDesc())
				.site(source.getSite())
				.tickerSymbol(source.getTickerSymbol())
				.type(source.getType())
				.website(source.getWebsite())
				.build();
		
		return account;
	}
}