package com.nowellpoint.aws.api.resource;

import java.io.IOException;
import java.net.URI;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
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
import com.nowellpoint.aws.api.util.HttpServletRequestUtil;
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
	private HttpServletRequest servletRequest;
	
	@GET
	@Path("/{id}/picture")
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	public Response get(@PathParam(value="id") String id) {
		//HttpServletRequestUtil.getSubject(servletRequest);
		
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
	
	@GET
	@Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getIdentity(@PathParam("id") String id) {
		String subject = HttpServletRequestUtil.getSubject(servletRequest);
		
		IdentityDTO resource = identityService.findIdentity( id, subject );
		
		return Response.ok(resource)
				.build();
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createIdentity(IdentityDTO resource) {
		String subject = HttpServletRequestUtil.getSubject(servletRequest);
		
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
		String subject = HttpServletRequestUtil.getSubject(servletRequest);
		
		resource.setId(id);
		
		identityService.updateIdentity( subject, resource, uriInfo.getBaseUri() );
		
		return Response.ok(resource).build();
	}
	
	@GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getIdentityBySubject(@QueryParam("subject") String subject) {
		HttpServletRequestUtil.getSubject(servletRequest);
		
		IdentityDTO resource = identityService.findIdentityBySubject( subject );
		
		return Response.ok(resource)
				.build();
	}
	
	@POST
	@Path("/{id}/salesforce-profile")
	@Produces(MediaType.APPLICATION_JSON)
	public Response addSalesforceProfile(@PathParam("id") String id, SalesforceProfile salesforceProfile) {
		String subject = HttpServletRequestUtil.getSubject(servletRequest);
		
		IdentityDTO resource = identityService.findIdentity(id, subject);
		
		String profileHref = salesforceProfile.getPhotos().getProfilePicture() + "?oauth_token=" + salesforceService.findTokenBySubject( subject ).getAccessToken();
		
		System.out.println(IdentityResource.class.getName() + " " + profileHref);
		
		URI uri = identityService.addSalesforceProfilePicture(id, profileHref, uriInfo.getBaseUri() );
		
		System.out.println(IdentityResource.class.getName() + " " + uri.toString());
		
		salesforceProfile.getPhotos().setProfilePicture(uri.toString());
		
		resource.addSalesforceProfile(salesforceProfile);
		
		identityService.updateIdentity( subject, resource, uriInfo.getBaseUri() );
		
		return Response.ok(resource).build();
	}
	
	@PUT
	@Path("/{id}/salesforce-profile")
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateSalesforceProfile(@PathParam("id") String id, SalesforceProfile salesforceProfile) {
		String subject = HttpServletRequestUtil.getSubject(servletRequest);
		
		IdentityDTO resource = identityService.findIdentity(id, subject);
		
		resource.addSalesforceProfile(salesforceProfile);
		
		identityService.updateIdentity( subject, resource, uriInfo.getBaseUri() );
		
		return Response.ok(resource).build();
	}
	
	@DELETE
	@Path("/{id}/salesforce-profile/{userId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response removeSalesforceProfile(@PathParam("id") String id, String userId) {
		String subject = HttpServletRequestUtil.getSubject(servletRequest);
		
		IdentityDTO resource = identityService.findIdentity(id, subject);
		
		resource.removeSalesforceProfile(userId);
		
		identityService.updateIdentity( subject, resource, uriInfo.getBaseUri() );
		
		return Response.ok(resource).build();
	}
	
//	@POST
//	@Path("/{id}/picture")
//	@Consumes(MediaType.MULTIPART_FORM_DATA)
//	public Response uploadProfileImage(MultipartFormDataInput input) {
//		
//		Map<String, List<InputPart>> formParts = input.getFormDataMap();
//
//		List<InputPart> inPart = formParts.get("file");
//
//		for (InputPart inputPart : inPart) {
//			try {
//				InputStream inputStream = inputPart.getBody(InputStream.class,null);
//				
//				AmazonS3 s3Client = new AmazonS3Client();
//					
//				ObjectMetadata objectMetadata = new ObjectMetadata();
//			    objectMetadata.setContentLength(inputStream.available());
//					
//			    PutObjectRequest putObjectRequest = new PutObjectRequest("salesforce-outbound-messages", UUID.randomUUID().toString(), inputStream, objectMetadata);
//			    	
//			    s3Client.putObject(putObjectRequest);	
//				
//			} catch (IOException e) {
//				throw new WebApplicationException(e, Status.INTERNAL_SERVER_ERROR);
//			}
//		}
//		
//		return Response.ok()
//				.build();
//	}
}