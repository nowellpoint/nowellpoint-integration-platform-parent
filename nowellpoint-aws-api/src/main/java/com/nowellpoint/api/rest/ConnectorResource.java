package com.nowellpoint.api.rest;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.nowellpoint.api.rest.domain.ConnectorStatusRequest;

@Path("/connectors")
public interface ConnectorResource {
	
	@GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getConnectors();
	
	@GET
	@Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getConnector(@PathParam("id") String id);

	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public Response createConnector(
			@FormParam("type") String type,
			@FormParam("name") String name, 
			@FormParam("clientId") String clientId, 
			@FormParam("clientSecret") String clientSecret, 
			@FormParam("username") String username, 
			@FormParam("password") String password);
	
	@POST
	@Path("{id}")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateConnector(@PathParam("id") String id, 
			@FormParam("name") String name, 
			@FormParam("clientId") String clientId, 
			@FormParam("clientSecret") String clientSecret, 
			@FormParam("username") String username, 
			@FormParam("password") String password);
	
	@DELETE
	@Path("{id}")
	public Response deleteConnector(@PathParam("id") String id);
	
	@POST
	@Path("{id}/actions/{action}/invoke")
	@Produces(MediaType.APPLICATION_JSON)
	public Response invokeAction(@PathParam(value="id") String id, 
			@PathParam(value="action") String action);
	
	@POST
	@Path("{id}/status")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateConnectorStatus(@PathParam("id") String id, ConnectorStatusRequest request);
}