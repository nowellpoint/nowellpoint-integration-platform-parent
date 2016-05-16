package com.nowellpoint.aws.api.resource;

import java.io.IOException;
import java.util.Optional;
import java.util.Set;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.Response.Status;

import com.nowellpoint.aws.api.dto.PropertyDTO;
import com.nowellpoint.aws.api.service.IdentityProviderService;
import com.nowellpoint.aws.api.service.PropertyService;
import com.nowellpoint.aws.idp.model.Account;
import com.nowellpoint.aws.idp.model.Group;

@Path("/properties")
public class PropertyResource {
	
	@Inject
	private PropertyService propertyService;
	
	@Inject
	private IdentityProviderService identityProviderService;
	
	@Context
	private UriInfo uriInfo;
	
	@Context
	private SecurityContext securityContext;
	
	@GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response findAll() {
		String subject = securityContext.getUserPrincipal().getName();
		
		Account account;
		try {
			account = identityProviderService.getAccountBySubject(subject);
		} catch (IOException e) {
			throw new WebApplicationException(e, Status.INTERNAL_SERVER_ERROR);
		}
		
		Optional<Group> group = account.getGroups().getItems().stream().filter(p -> "System Administrator".equals(p.getName())).findFirst();
		
		if (! group.isPresent()) {
			throw new NotAuthorizedException("Unauthorized: your account is not authorized to access this resource");
		}
		
		Set<PropertyDTO> resources = propertyService.getProperties();
		
		return Response.ok(resources).build();
    }
}