package com.nowellpoint.aws.api.resource;

import static com.nowellpoint.aws.data.CacheManager.deserialize;
import static com.nowellpoint.aws.data.CacheManager.getCache;
import static com.nowellpoint.aws.data.CacheManager.serialize;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.annotation.security.PermitAll;
import javax.inject.Inject;
import javax.net.ssl.HttpsURLConnection;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.DeleteObjectsRequest;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.util.IOUtils;
import com.nowellpoint.aws.api.dto.AccountProfileDTO;
import com.nowellpoint.aws.api.dto.SalesforceConnectorDTO;
import com.nowellpoint.aws.api.dto.ServiceProviderDTO;
import com.nowellpoint.aws.api.service.AccountProfileService;
import com.nowellpoint.aws.api.service.SalesforceConnectorService;
import com.nowellpoint.aws.api.service.SalesforceService;
import com.nowellpoint.aws.api.service.ServiceProviderService;
import com.nowellpoint.aws.data.mongodb.Environment;
import com.nowellpoint.aws.data.mongodb.EnvironmentVariable;
import com.nowellpoint.client.sforce.OauthAuthenticationResponse;
import com.nowellpoint.client.sforce.OauthException;
import com.nowellpoint.client.sforce.model.Token;

import redis.clients.jedis.Jedis;

@Path("/salesforce")
public class SalesforceConnectorResource {
	
	@Inject
	private AccountProfileService accountProfileService;
	
	@Inject
	private SalesforceService salesforceService;
	
	@Inject
	private SalesforceConnectorService salesforceConnectorService;
	
	@Inject
	private ServiceProviderService serviceProviderService;
	
	@Context
	private SecurityContext securityContext;
	
	@Context
	private UriInfo uriInfo;
	
	@GET
	@Path("connectors")
    @Produces(MediaType.APPLICATION_JSON)
    public Response findAll() {
		String subject = securityContext.getUserPrincipal().getName();
		
		Set<SalesforceConnectorDTO> resources = salesforceConnectorService.getAll(subject);
		
		return Response.ok(resources)
				.build();
    }
	
	@GET
	@Path("connector")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getSalesforceConnectorDetails(@QueryParam(value="code") String code) {
		String subject = securityContext.getUserPrincipal().getName();
		
		OauthAuthenticationResponse response = null;
		
		try {
			response = salesforceService.authenticate(code);
		} catch (OauthException e) {
			throw new WebApplicationException(e.getErrorDescription(), Status.BAD_REQUEST);
		}
		
		Token token = response.getToken();
		
		putToken(subject, token.getId(), token);
		
		SalesforceConnectorDTO resource = salesforceService.getSalesforceInstance(token.getAccessToken(), token.getId());
		
		return Response.ok(resource).build();
	}
	
	@GET
	@Path("connector/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getSalesforceConnector(@PathParam(value="id") String id) {
		String subject = securityContext.getUserPrincipal().getName();
		
		SalesforceConnectorDTO resource = salesforceConnectorService.findSalesforceConnector(subject, id);
		
		return Response.ok(resource).build();
	}
	
	@PermitAll
	@GET
	@Path("connector/profilephoto/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getProfilePhoto(@PathParam(value="id") String id) {
		AmazonS3 s3Client = new AmazonS3Client();
		
		GetObjectRequest getObjectRequest = new GetObjectRequest("nowellpoint-profile-photos", id);
    	
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
	
	@POST
	@Path("connector")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public Response createSalesforceConnector(@FormParam(value="id") String id) {
		String subject = securityContext.getUserPrincipal().getName();
		
		Token token = getToken(subject, id);
		
		AccountProfileDTO owner = accountProfileService.findAccountProfileBySubject(subject);	
		
		SalesforceConnectorDTO resource = salesforceService.getSalesforceInstance(token.getAccessToken(), token.getId());
		resource.setOwner(owner);
		resource.setSubject(subject);
		resource.setEventSource(uriInfo.getBaseUri());
		resource.getIdentity().getPhotos().setPicture(putImage(token.getAccessToken(), resource.getIdentity().getPhotos().getPicture()));
		resource.getIdentity().getPhotos().setThumbnail(putImage(token.getAccessToken(), resource.getIdentity().getPhotos().getThumbnail()));
		
		salesforceConnectorService.createSalesforceConnector(resource);
		
		URI uri = UriBuilder.fromUri(uriInfo.getBaseUri())
				.path(SalesforceResource.class)
				.path("/{id}")
				.build(resource.getId());
		
		return Response.created(uri)
				.entity(resource)
				.build(); 
	}
	
	@DELETE
	@Path("connector/{id}")
	public Response deleteSalesforceConnector(@PathParam(value="id") String id) {
		String subject = securityContext.getUserPrincipal().getName();
		
		SalesforceConnectorDTO resource = salesforceConnectorService.findSalesforceConnector(subject, id);
		
		AmazonS3 s3Client = new AmazonS3Client();
		
		DeleteObjectsRequest deleteObjectsRequest = new DeleteObjectsRequest("outboundmessage")
				.withKeys(
						resource.getIdentity().getPhotos().getPicture().substring(resource.getIdentity().getPhotos().getPicture().lastIndexOf("/") + 1),
						resource.getIdentity().getPhotos().getPicture().substring(resource.getIdentity().getPhotos().getThumbnail().lastIndexOf("/") + 1)
					);
		
		s3Client.deleteObjects(deleteObjectsRequest);
		
		salesforceConnectorService.deleteSalesforceConnector(id, subject);
		
		return Response.noContent()
				.build(); 
	}
	
	@POST
	@Path("connector/{id}/service")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public Response addService(
			@PathParam(value="id") String id,
			@FormParam(value="serviceProviderId") String serviceProviderId) {
		
		String subject = securityContext.getUserPrincipal().getName();
		
		ServiceProviderDTO provider = serviceProviderService.getServiceProvider(serviceProviderId);
		
		SalesforceConnectorDTO resource = salesforceConnectorService.addService(provider, subject, id);
		
		URI uri = UriBuilder.fromUri(uriInfo.getBaseUri())
				.path(SalesforceConnectorResource.class)
				.path("/{id}")
				.build(resource.getId());
		
		return Response.created(uri)
				.entity(resource)
				.build(); 	
	}
	
	@POST
	@Path("connector/{id}/service/{key}/environments")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response saveEnvironments(
			@PathParam(value="id") String id,
			@PathParam(value="key") String key,
			Set<Environment> environments) {
		
		String subject = securityContext.getUserPrincipal().getName();
		
		SalesforceConnectorDTO resource = salesforceConnectorService.addEnvironments(subject, id, key, environments);
		
		return Response.ok(resource)
				.build(); 
	}
	
	@POST
	@Path("connector/{id}/service/{key}/variables")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response saveEnvironmentVariables(
			@PathParam(value="id") String id,
			@PathParam(value="key") String key,
			Set<EnvironmentVariable> environmentVariables) {
		
		String subject = securityContext.getUserPrincipal().getName();
		
		SalesforceConnectorDTO resource = null;
		try {
			resource = salesforceConnectorService.addEnvironmentVariables(subject, id, key, null, environmentVariables);
		} catch (UnsupportedOperationException e) {
			throw new BadRequestException(e.getMessage());
		}
		
		return Response.ok(resource)
				.build(); 
		
	}		
	
	@POST
	@Path("connector/{id}/service/{key}/variables/{environment}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response saveEnvironmentVariables(
			@PathParam(value="id") String id,
			@PathParam(value="key") String key,
			@PathParam(value="environment") String environment,
			Set<EnvironmentVariable> environmentVariables) {
		
		String subject = securityContext.getUserPrincipal().getName();
		
		SalesforceConnectorDTO resource = null;
		try {
			resource = salesforceConnectorService.addEnvironmentVariables(subject, id, key, environment, environmentVariables);
		} catch (UnsupportedOperationException e) {
			throw new BadRequestException(e.getMessage());
		}
		
		return Response.ok(resource)
				.build(); 
		
	}
	
	@POST
	@Path("connector/{id}/service/{key}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response addServiceConfiguration(
			@PathParam(value="id") String id,
			@PathParam(value="key") String key,
			Map<String,Object> configParams) {
		
		String subject = securityContext.getUserPrincipal().getName();
		
		salesforceConnectorService.addServiceConfiguration(subject, id, key, configParams);
		
		salesforceService.buildPackage(key);
		
		return Response.ok()
				.build(); 
	}
	
	@DELETE
	@Path("connector/{id}/service/{key}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteService(
			@PathParam(value="id") String id,
			@PathParam(value="key") String key) {
		
		String subject = securityContext.getUserPrincipal().getName();
		
		SalesforceConnectorDTO resource = salesforceConnectorService.removeService(subject, id, key);
		
		return Response.ok()
				.entity(resource)
				.build(); 
		
	}
	
	private void putToken(String subject, String userId, Token token) {
		Jedis jedis = getCache();
		try {
			jedis.hset(subject.getBytes(), Token.class.getName().concat( userId ).getBytes(), serialize(token));
		} finally {
			jedis.close();
		}
	}
	
	private Token getToken(String subject, String userId) {
		Jedis jedis = getCache();
		byte[] bytes = null;
		try {
			bytes = jedis.hget(subject.getBytes(), Token.class.getName().concat( userId ).getBytes());
		} finally {
			jedis.close();
		}
		
		Token token = null;
		if (bytes != null) {
			token = deserialize(bytes, Token.class);
		}
		
		return token;
	}
	
	private String putImage(String accessToken, String imageUrl) {
		
		AmazonS3 s3Client = new AmazonS3Client();
		
		try {
			URL url = new URL( imageUrl + "?oauth_token=" + accessToken );
			
			HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
			String contentType = connection.getHeaderField("Content-Type");
			
			ObjectMetadata objectMetadata = new ObjectMetadata();
	    	objectMetadata.setContentLength(connection.getContentLength());
	    	objectMetadata.setContentType(contentType);
	    	
	    	String key = UUID.randomUUID().toString();
			
	    	PutObjectRequest putObjectRequest = new PutObjectRequest("nowellpoint-profile-photos", key, connection.getInputStream(), objectMetadata);
	    	
	    	s3Client.putObject(putObjectRequest);
	    	
	    	URI uri = UriBuilder.fromUri(uriInfo.getBaseUri())
					.path(SalesforceConnectorResource.class)
					.path("connector")
					.path("profilephoto")
					.path("{id}")
					.build(key);
			
			return uri.toString();
			
		} catch (IOException e) {
			throw new WebApplicationException(e, Status.INTERNAL_SERVER_ERROR);
		}
	}
}