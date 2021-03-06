package com.nowellpoint.api.rest;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.nowellpoint.api.rest.domain.ConnectorRequest;

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
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response createConnector(ConnectorRequest request);
	
	@POST
	@Path("{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateConnector(@PathParam("id") String id, ConnectorRequest request);
	
	@DELETE
	@Path("{id}")
	public Response deleteConnector(@PathParam("id") String id);
	
	@POST
	@Path("{id}/status")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateConnectorStatus(@PathParam("id") String id, ConnectorRequest request);
}