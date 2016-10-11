package com.nowellpoint.api.resource;

import java.util.Set;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.nowellpoint.api.model.dto.Plan;
import com.nowellpoint.api.service.PlanService;

@Path("plans")
public class PlanResource {
	
	@Inject
	private PlanService planService;
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAllActive(@QueryParam(value="localeSidKey") String localeSidKey, @QueryParam(value="languageLocaleKey") String languageLocaleKey) {
		Set<Plan> plans = planService.getAllActive(localeSidKey, languageLocaleKey);
		
		return Response.ok(plans)
				.build();
	}
}