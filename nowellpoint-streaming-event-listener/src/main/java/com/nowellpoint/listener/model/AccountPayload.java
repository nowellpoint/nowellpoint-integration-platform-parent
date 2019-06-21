package com.nowellpoint.listener.model;

import java.util.Date;

import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonProperty;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class AccountPayload {
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