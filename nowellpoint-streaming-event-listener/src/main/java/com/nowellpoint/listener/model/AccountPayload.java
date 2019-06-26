package com.nowellpoint.listener.model;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.Map;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;
import javax.json.bind.config.PropertyNamingStrategy;
import javax.json.bind.config.PropertyVisibilityStrategy;

import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonProperty;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AccountPayload {
	private static final Jsonb jsonb = JsonbBuilder.create(new JsonbConfig()
			.withNullValues(Boolean.TRUE)
			.withPropertyNamingStrategy(PropertyNamingStrategy.UPPER_CAMEL_CASE)
			.withPropertyVisibilityStrategy(
					new PropertyVisibilityStrategy() {
						
						@Override
						public boolean isVisible(Field field) {
							return true;
						}
						
						@Override
						public boolean isVisible(Method method) {
							return false;
						}
						
					}));
	
	private String accountNumber;
	private String accountSource;
	private Double annualRevenue;
	private Address billingAddress;
	private String createdById;
	private Date createdDate;
	private String description;
	private String name;
	private Integer numberOfEmployees;
	private String industry;
	private String lastModifiedById;
	private Date lastModifiedDate;
	private String ownerId;
	private String ownership;
	private String phone;
	private String rating;
	private Address shippingAddress;
	private String sic;
	private String sicDesc;
	private String site;
	private String tickerSymbol;
	private String type;
	private String website;
	
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
	
	public static AccountPayload of(Map<String,Object> attributes) {
		if (attributes == null)
			return null;
		
		String json = jsonb.toJson(attributes);
		return jsonb.fromJson(json, AccountPayload.class);
	}
}