package com.nowellpoint.api.resource;

import javax.annotation.security.PermitAll;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.nowellpoint.api.rest.PlanService;
import com.nowellpoint.api.rest.domain.Plan;
import com.nowellpoint.api.rest.domain.PlanList;

@Path("plans")
public class PlanResource {
	
	@Inject
	private PlanService planService;
	
	@GET
	@PermitAll
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAllActive(@QueryParam(value="localeSidKey") String localeSidKey, @QueryParam(value="languageSidKey") String languageSidKey) {
		
		PlanList resources = planService.getAllActive(localeSidKey, languageSidKey);
		
		return Response.ok(resources)
				.build();
	}
	
	@GET
	@PermitAll
	@Path("{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response findById(@PathParam(value="id") String id) {
		
		Plan plan = planService.findById(id);
		
		return Response.ok(plan)
				.build();
		
	}
}