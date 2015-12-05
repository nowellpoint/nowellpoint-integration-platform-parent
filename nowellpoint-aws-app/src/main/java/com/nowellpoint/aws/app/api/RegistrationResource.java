package com.nowellpoint.aws.app.api;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.nowellpoint.aws.client.IdentityProviderClient;
import com.nowellpoint.aws.model.idp.GetTokenRequest;
import com.nowellpoint.aws.model.idp.GetTokenResponse;

@Path("/registration")
public class RegistrationResource {

	@GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response findAll() {
		
		
		
		return null;
    }
}