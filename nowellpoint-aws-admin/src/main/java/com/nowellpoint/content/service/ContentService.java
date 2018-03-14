package com.nowellpoint.content.service;

import java.util.List;
import java.util.logging.Logger;

import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectIdBuilder;
import com.nowellpoint.content.model.IsoCountry;
import com.nowellpoint.content.model.IsoCountryList;
import com.nowellpoint.content.model.Plan;
import com.nowellpoint.content.model.PlanList;

public class ContentService extends S3ObjectService {
	
	private static final Logger LOG = Logger.getLogger(ContentService.class.getName());
	private static final String S3_BUCKET = "nowellpoint-static-content";
	
	public IsoCountryList getCountries() {
		
		LOG.info("Retrieving the list of countries from the content repository");
		
		S3ObjectIdBuilder builder = new S3ObjectIdBuilder();
		builder.setBucket(S3_BUCKET);
		builder.setKey("countries.json");
		
		GetObjectRequest request = new GetObjectRequest(builder.build());
		
		S3Object object = s3client.getObject(request);	
		
		List<IsoCountry> countries = readCollection(IsoCountry.class, object);
		
		return new IsoCountryList(countries);
	}
	
	public PlanList getPlans() {
		
		LOG.info("Retrieving the list of plans from the content repository");
		
		S3ObjectIdBuilder builder = new S3ObjectIdBuilder();
		builder.setBucket(S3_BUCKET);
		builder.setKey("plans.json");
		
		GetObjectRequest request = new GetObjectRequest(builder.build());
		
		S3Object object = s3client.getObject(request);	
		
		List<Plan> plans = readCollection(Plan.class, object);
		
		return new PlanList(plans);
	}
}