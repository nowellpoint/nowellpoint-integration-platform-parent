package com.nowellpoint.api.rest;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

import io.swagger.annotations.Api;

@Path("communications")
@Api(value = "/communications")
public interface CommunicationResource {
	
	@POST
	@Path("slack/actions/{action}/invoke")
	public Response invokeAction(@PathParam("action") String action, @FormParam("webhookUrl") String webhookUrl);
	
}