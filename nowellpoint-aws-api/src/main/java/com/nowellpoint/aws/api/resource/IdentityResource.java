package com.nowellpoint.aws.api.resource;

import java.io.IOException;
import java.net.URI;

import javax.annotation.security.PermitAll;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.util.IOUtils;
import com.nowellpoint.aws.api.dto.IdentityDTO;
import com.nowellpoint.aws.api.service.IdentityService;
import com.nowellpoint.aws.api.service.SalesforceService;
import com.nowellpoint.aws.data.mongodb.SalesforceProfile;

@Path("/identity")
public class IdentityResource {
	
	@Inject
	private IdentityService identityService;
	
	@Inject
	private SalesforceService salesforceService;

	@Context
	private UriInfo uriInfo;
	
	@Context 
	private SecurityContext securityContext;
	
	/**
	 * @api {get} /identity/:id/picture Get Profile Picture
	 * @apiName getPicture
	 * @apiVersion 1.0.0
	 * @apiGroup Identity
	 * 
	 * @apiParam {String} id The Identity's unique id
	 */
	
	@GET
	@Path("/{id}/picture")
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	@PermitAll
	public Response getPicture(@PathParam(value="id") String id) {
		
		AmazonS3 s3Client = new AmazonS3Client();
		
		GetObjectRequest getObjectRequest = new GetObjectRequest("aws-microservices", id);
    	
    	S3Object image = s3Client.getObject(getObjectRequest);
    	
    	String contentType = image.getObjectMetadata().getContentType();
    	
    	byte[] bytes = null;
    	try {
    		bytes = IOUtils.toByteArray(image.getObjectContent());    		
		} catch (IOException e) {
			throw new WebApplicationException( e.getMessage(), Status.INTERNAL_SERVER_ERROR );
		} finally {
			try {
				image.close();
			} catch (IOException ignore) {

			}
		}
    	
    	return Response.ok().entity(bytes)
    			.header("Content-Disposition", "inline; filename=\"" + id + "\"")
    			.header("Content-Length", bytes.length)
    			.header("Content-Type", contentType)
    			.build();
	}
	
	/**
	 * @api {get} /identity/:id Get Identity
	 * @apiName getIdentity
	 * @apiVersion 1.0.0
	 * @apiGroup Identity
	 * @apiHeader {String} authorization Authorization with the value of Bearer access_token from authenticate
	 * 
	 * @apiParam {String} id The Identity's unique id
	 */
	
	@GET
	@Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getIdentity(@PathParam("id") String id) {
		String subject = securityContext.getUserPrincipal().getName();
		
		IdentityDTO resource = identityService.findIdentity( id, subject );
		
		return Response.ok(resource)
				.build();
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createIdentity(IdentityDTO resource) {
		String subject = securityContext.getUserPrincipal().getName();
		
		identityService.createIdentity( subject, resource, uriInfo.getBaseUri() );
		
		URI uri = UriBuilder.fromUri(uriInfo.getBaseUri())
				.path(IdentityResource.class)
				.path("/{id}")
				.build(resource.getId());
		
		return Response.created(uri).build();
	}
	
	@PUT
	@Path("/{id}")
	@Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateIdentity(@PathParam("id") String id, IdentityDTO resource) {
		String subject = securityContext.getUserPrincipal().getName();
		
		resource.setId(id);
		
		identityService.updateIdentity( subject, resource, uriInfo.getBaseUri() );
		
		return Response.ok(resource).build();
	}
	
	@GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getIdentityBySubject(@QueryParam("subject") String subject) {
		
		IdentityDTO resource = identityService.findIdentityBySubject( subject );
		
		return Response.ok(resource)
				.build();
	}
	
	@POST
	@Path("/{id}/salesforce-profile")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response addSalesforceProfile(@PathParam("id") String id, SalesforceProfile salesforceProfile) {
		String subject = securityContext.getUserPrincipal().getName();
		
		IdentityDTO resource = identityService.findIdentity(id, subject);
		
		String profileHref = salesforceProfile.getPhotos().getProfilePicture() + "?oauth_token=" + salesforceService.findToken( subject, salesforceProfile.getUserId() ).getAccessToken();
		
		identityService.addSalesforceProfilePicture( salesforceProfile.getUserId(), profileHref );
		
		URI uri = UriBuilder.fromUri(uriInfo.getBaseUri())
				.path(IdentityResource.class)
				.path("{id}")
				.path("salesforce")
				.path("{userId}")
				.build(id, salesforceProfile.getUserId());
		
		salesforceProfile.getPhotos().setProfilePicture(uri.toString());
		
		resource.addSalesforceProfile(salesforceProfile);
		
		identityService.updateIdentity( subject, resource, uriInfo.getBaseUri() );
		
		return Response.ok(resource).build();
	}
	
	@PUT
	@Path("/{id}/salesforce-profile/{userId}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateSalesforceProfile(@PathParam("id") String id, @PathParam("userId") String userId, SalesforceProfile salesforceProfile) {
		String subject = securityContext.getUserPrincipal().getName();
		
		salesforceProfile.setUserId(userId);
		
		IdentityDTO resource = identityService.findIdentity(id, subject);
		
		resource.addSalesforceProfile(salesforceProfile);
		
		identityService.updateIdentity( subject, resource, uriInfo.getBaseUri() );
		
		return Response.ok(resource).build();
	}
	
	@DELETE
	@Path("/{id}/salesforce-profile/{userId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response removeSalesforceProfile(@PathParam("id") String id, @PathParam("userId") String userId) {
		String subject = securityContext.getUserPrincipal().getName();
		
		IdentityDTO resource = identityService.findIdentity(id, subject);
		
		resource.removeSalesforceProfile(userId);
		
		identityService.updateIdentity( subject, resource, uriInfo.getBaseUri() );
		
		return Response.ok(resource).build();
	}
}