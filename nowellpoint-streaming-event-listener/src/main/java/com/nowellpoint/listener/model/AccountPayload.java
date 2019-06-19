package com.nowellpoint.listener.model;

import java.util.Date;

import javax.json.bind.annotation.JsonbProperty;

import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonProperty;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class AccountPayload {
	@Getter @JsonbProperty("AccountNumber") private String accountNumber;
	@Getter @JsonbProperty("AccountSource") private String accountSource;
	@Getter @JsonbProperty("AnnualRevenue") private Double annualRevenue;
	@Getter @JsonbProperty("BillingAddress") private Address billingAddress;
	@Getter @JsonbProperty("CreatedById") private String createdById;
	@Getter @JsonbProperty("CreatedDate") private Date createdDate;
	@Getter @JsonbProperty("Description") private String description;
	@Getter @JsonbProperty("Name") private String name;
	@Getter @JsonbProperty("NumberOfEmployees") private Integer numberOfEmployees;
	@Getter @JsonbProperty("Industry") private String industry;
	@Getter @JsonbProperty("LastModifiedById") private String lastModifiedById;
	@Getter @JsonbProperty("LastModifiedDate") private Date lastModifiedDate;
	@Getter @JsonbProperty("OwnerId") private String ownerId;
	@Getter @JsonbProperty("Ownership") private String ownership;
	@Getter @JsonbProperty("Phone") private String phone;
	@Getter @JsonbProperty("Rating") private String rating;
	@Getter @JsonbProperty("ShippingAddress") private Address shippingAddress;
	@Getter @JsonbProperty("Sic") private String sic;
	@Getter @JsonbProperty("SicDesc") private String sicDesc;
	@Getter @JsonbProperty("Site") private String site;
	@Getter @JsonbProperty("TickerSymbol") private String tickerSymbol;
	@Getter @JsonbProperty("Type") private String type;
	@Getter @JsonbProperty("Website") private String website;
	
	@BsonCreator
	public AccountPayload(@BsonProperty("accountNumber") String accountNumber,
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
			@BsonProperty("website") String website) {
		
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
	}
}