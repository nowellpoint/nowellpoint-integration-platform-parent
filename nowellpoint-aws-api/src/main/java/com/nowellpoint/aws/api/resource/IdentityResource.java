package com.nowellpoint.aws.api.resource;

import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
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
import com.nowellpoint.aws.api.util.HttpServletRequestUtil;

@Path("/identity")
public class IdentityResource {
	
	@Inject
	private IdentityService identityService;

	@Context
	private UriInfo uriInfo;
	
	@Context
	private HttpServletRequest servletRequest;
	
	@GET
	@Path("/{id}/picture")
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	public Response get(@PathParam(value="id") String id) {
		//String subject = HttpServletRequestUtil.getSubject(servletRequest);
		
		//IdentityDTO resource = identityService.findIdentityBySubject( subject );
		
		AmazonS3 s3Client = new AmazonS3Client();
		
		GetObjectRequest getObjectRequest = new GetObjectRequest("aws-microservices", id);
    	
    	S3Object image = s3Client.getObject(getObjectRequest);
    	
    	byte[] s = null;
    	try {
    		s = IOUtils.toByteArray(image.getObjectContent());
    		image.close();
		} catch (IOException e) {
			throw new WebApplicationException( e.getMessage(), Status.INTERNAL_SERVER_ERROR );
		} 
    	
    	return Response.ok()
    			.entity(s)
    			.header("Content-Disposition", "inline; filename=\"" + id + "\"")
    			.header("Content-Length", s.length)
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
		
		identityService.create( subject, resource, uriInfo.getBaseUri() );
		
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