package com.nowellpoint.content.service;

import java.util.List;

import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectIdBuilder;
import com.nowellpoint.content.model.IsoCountry;
import com.nowellpoint.content.model.IsoCountryList;

public class IsoCountryService extends S3ObjectService<IsoCountry> {
	
	public IsoCountryList getCountries() {
		S3ObjectIdBuilder builder = new S3ObjectIdBuilder();
		builder.setBucket("nowellpoint-static-content");
		builder.setKey("countries.json");
		
		GetObjectRequest request = new GetObjectRequest(builder.build());
		
		S3Object object = s3client.getObject(request);	
		
		List<IsoCountry> countries = readCollection(IsoCountry.class, object);
		
		return new IsoCountryList(countries);
	}
}