package com.nowellpoint.content.service;

import java.util.List;

import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectIdBuilder;
import com.nowellpoint.content.model.Plan;
import com.nowellpoint.content.model.PlanList;

public class PlanService extends S3ObjectService<Plan> {

	public PlanList getPlans() {
		S3ObjectIdBuilder builder = new S3ObjectIdBuilder();
		builder.setBucket("nowellpoint-static-content");
		builder.setKey("plans.json");
		
		GetObjectRequest request = new GetObjectRequest(builder.build());
		
		S3Object object = s3client.getObject(request);	
		
		List<Plan> plans = readCollection(Plan.class, object);
		
		return new PlanList(plans);
	}
}