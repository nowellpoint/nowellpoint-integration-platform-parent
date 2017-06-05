package com.nowellpoint.api.rest;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("salesforce-connectors")
public interface SalesforceConnectorResource {
	
	@GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response findAllByOwner();
	
	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public Response createSalesforceConnector(
			@FormParam("id") String id, 
			@FormParam("instanceUrl") String instanceUrl, 
			@FormParam("accessToken") String accessToken, 
			@FormParam("refreshToken") String refreshToken);
	
	@GET
	@Path("{id}/sobjects")
	@Produces(MediaType.APPLICATION_JSON)
	public Response describeSObject(@PathParam(value="id") String id, @QueryParam(value="sobject") String sobject);
	
	@GET
	@Path("{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getSalesforceConnector(@PathParam(value="id") String id, @QueryParam(value="expand") String expand);
	
	@POST
	@Path("{id}")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateSalesforceConnector(@PathParam(value="id") String id, 
			@FormParam(value="name") String name, 
			@FormParam(value="tag") String tag,
			@FormParam(value="ownerId") String ownerId);
	
	@DELETE
	@Path("{id}")
	public Response deleteSalesforceConnector(@PathParam(value="id") String id);
	
	@POST
	@Path("{id}/actions/{action}/invoke")
	@Produces(MediaType.APPLICATION_JSON)
	public Response invokeAction(@PathParam(value="id") String id, @PathParam(value="action") String action);
}