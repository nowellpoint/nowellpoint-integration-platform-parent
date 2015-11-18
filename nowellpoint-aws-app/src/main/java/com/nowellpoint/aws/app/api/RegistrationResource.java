package com.nowellpoint.aws.app.api;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/registration")
public class RegistrationResource {
	
	public RegistrationResource() {
		
	}

	@GET
    @Produces(MediaType.APPLICATION_JSON)
    public String findAll() {
        return "wildfly-swarm-response";
    }
}