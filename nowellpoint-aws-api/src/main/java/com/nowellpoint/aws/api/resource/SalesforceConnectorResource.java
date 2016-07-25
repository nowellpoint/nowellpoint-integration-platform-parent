package com.nowellpoint.aws.api.resource;

import static com.nowellpoint.aws.data.CacheManager.deserialize;
import static com.nowellpoint.aws.data.CacheManager.getCache;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
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
import com.amazonaws.services.s3.model.DeleteObjectsRequest.KeyVersion;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.util.IOUtils;
import com.nowellpoint.aws.api.dto.AccountProfileDTO;
import com.nowellpoint.aws.api.dto.EnvironmentDTO;
import com.nowellpoint.aws.api.dto.EnvironmentVariableDTO;
import com.nowellpoint.aws.api.dto.EventListenerDTO;
import com.nowellpoint.aws.api.dto.SalesforceConnectorDTO;
import com.nowellpoint.aws.api.dto.ServiceInstanceDTO;
import com.nowellpoint.aws.api.model.Targets;
import com.nowellpoint.aws.api.service.AccountProfileService;
import com.nowellpoint.aws.api.service.SalesforceConnectorService;
import com.nowellpoint.aws.api.service.SalesforceService;
import com.nowellpoint.client.sforce.model.Token;
import com.sforce.soap.partner.Field;
import com.sforce.soap.partner.sobject.SObject;

import redis.clients.jedis.Jedis;

@Path("connectors")
public class SalesforceConnectorResource {
	
	@Inject
	private AccountProfileService accountProfileService;
	
	@Inject
	private SalesforceService salesforceService;
	
	@Inject
	private SalesforceConnectorService salesforceConnectorService;
	
	@Context
	private SecurityContext securityContext;
	
	@Context
	private UriInfo uriInfo;
	
	@GET
	@Path("salesforce")
    @Produces(MediaType.APPLICATION_JSON)
    public Response findAll() {
		String subject = securityContext.getUserPrincipal().getName();
		
		Set<SalesforceConnectorDTO> resources = salesforceConnectorService.getAll(subject);
		
		return Response.ok(resources)
				.build();
    }
	
	@GET
	@Path("salesforce/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getSalesforceConnector(@PathParam(value="id") String id) {
		String subject = securityContext.getUserPrincipal().getName();
		
		SalesforceConnectorDTO resource = salesforceConnectorService.findSalesforceConnector(subject, id);
		
		return Response.ok(resource).build();
	}
	
	@PermitAll
	@GET
	@Path("salesforce/profilephoto/{id}")
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
	@Path("salesforce")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public Response createSalesforceConnector(@FormParam(value="id") String id) {
		String subject = securityContext.getUserPrincipal().getName();
		
		Token token = getToken(subject, id);
		
		AccountProfileDTO owner = accountProfileService.findAccountProfileBySubject(subject);	
		
		SalesforceConnectorDTO resource = salesforceService.getSalesforceInstance(token.getAccessToken(), token.getId());
		resource.setOwner(owner);
		resource.setSubject(subject);
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
	
	@POST
	@Path("salesforce/{id}")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateSalesforceConnector(@PathParam(value="id") String id, @FormParam(value="tag") String tag) {
		String subject = securityContext.getUserPrincipal().getName();
		
		SalesforceConnectorDTO resource = salesforceConnectorService.findSalesforceConnector(subject, id);
		resource.setSubject(subject);
		resource.setTag(tag);
		
		salesforceConnectorService.updateSalesforceConnector(resource);
		
		return Response.ok()
				.entity(resource)
				.build(); 
	}
	
	@DELETE
	@Path("salesforce/{id}")
	public Response deleteSalesforceConnector(@PathParam(value="id") String id) {
		String subject = securityContext.getUserPrincipal().getName();
		
		SalesforceConnectorDTO resource = salesforceConnectorService.findSalesforceConnector(subject, id);
		
		AmazonS3 s3Client = new AmazonS3Client();
		
		List<KeyVersion> keys = new ArrayList<KeyVersion>();
		keys.add(new KeyVersion(resource.getIdentity().getPhotos().getPicture().substring(resource.getIdentity().getPhotos().getPicture().lastIndexOf("/") + 1)));
		keys.add(new KeyVersion(resource.getIdentity().getPhotos().getThumbnail().substring(resource.getIdentity().getPhotos().getThumbnail().lastIndexOf("/") + 1)));
		
		DeleteObjectsRequest deleteObjectsRequest = new DeleteObjectsRequest("nowellpoint-profile-photos").withKeys(keys);
		
		s3Client.deleteObjects(deleteObjectsRequest);
		
		salesforceConnectorService.deleteSalesforceConnector(id, subject);
		
		return Response.noContent()
				.build(); 
	}

	@POST
	@Path("salesforce/{id}/providers/{serviceProviderId}/service/{serviceType}/plan/{code}")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public Response addServiceInstance(
			@PathParam(value="id") String id, 
			@PathParam(value="serviceProviderId") String serviceProviderId, 
			@PathParam(value="serviceType") String serviceType, 
			@PathParam(value="code") String code) {
		
		String subject = securityContext.getUserPrincipal().getName();
		
		SalesforceConnectorDTO resource = salesforceConnectorService.addServiceInstance(subject, id, serviceProviderId, serviceType, code);
		
		return Response.ok()
				.entity(resource)
				.build(); 	
	}
	
	@POST
	@Path("salesforce/{id}/service/{key}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateServiceInstance(
			@PathParam(value="id") String id,
			@PathParam(value="key") String key,
			ServiceInstanceDTO serviceInstance) {
		
		String subject = securityContext.getUserPrincipal().getName();
		
		SalesforceConnectorDTO resource = salesforceConnectorService.updateServiceInstance(subject, id, key, serviceInstance);
		
		return Response.ok()
				.entity(resource)
				.build(); 	
	}
	
	@POST
	@Path("salesforce/{id}/service/{key}/environments")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response addEnvironments(
			@PathParam(value="id") String id,
			@PathParam(value="key") String key,
			Set<EnvironmentDTO> environments) {
		
		String subject = securityContext.getUserPrincipal().getName();
		
		SalesforceConnectorDTO resource = salesforceConnectorService.addEnvironments(subject, id, key, environments);
		
		return Response.ok(resource)
				.build(); 
	}
	
	@POST
	@Path("salesforce/{id}/service/{key}/variables/{environment}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response addEnvironmentVariables(
			@PathParam(value="id") String id,
			@PathParam(value="key") String key,
			@PathParam(value="environment") String environmentName,
			Set<EnvironmentVariableDTO> environmentVariables) {
		
		String subject = securityContext.getUserPrincipal().getName();
		
		SalesforceConnectorDTO resource = null;
		try {
			resource = salesforceConnectorService.addEnvironmentVariables(subject, id, key, environmentName, environmentVariables);
		} catch (UnsupportedOperationException | IllegalArgumentException e) {
			throw new BadRequestException(e.getMessage());
		}
		
		return Response.ok(resource)
				.build(); 
		
	}
	
	@POST
	@Path("salesforce/{id}/service/{key}/deployment/{environment}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response deploy(
			@PathParam(value="id") String id,
			@PathParam(value="key") String key,
			@PathParam(value="environment") String environmentName) {
		
		String subject = securityContext.getUserPrincipal().getName();
		
		SalesforceConnectorDTO resource = null;
		try {
			resource = salesforceConnectorService.deploy(subject, id, key, environmentName);
		} catch (Exception e) {
			e.printStackTrace();
			throw new BadRequestException(e.getMessage());
		}
		
		return Response.ok(resource)
				.build(); 
		
	}
	
	@POST
	@Path("salesforce/{id}/service/{key}/listeners")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response addEventListeners(
			@PathParam(value="id") String id,
			@PathParam(value="key") String key,
			Set<EventListenerDTO> eventListeners) {
		
		String subject = securityContext.getUserPrincipal().getName();
		
		SalesforceConnectorDTO resource = null;
		try {
			resource = salesforceConnectorService.addEventListeners(subject, id, key, eventListeners);
		} catch (Exception e) {
			throw new BadRequestException(e.getMessage());
		}
		
		return Response.ok(resource)
				.build(); 
	}
	
	@POST
	@Path("salesforce/{id}/service/{key}/targets")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response addTargets(
			@PathParam(value="id") String id,
			@PathParam(value="key") String key,
			Targets targets) {
		
		String subject = securityContext.getUserPrincipal().getName();
		
		SalesforceConnectorDTO resource = null;
		try {
			resource = salesforceConnectorService.addTargets(subject, id, key, targets);
		} catch (UnsupportedOperationException | IllegalArgumentException e) {
			throw new BadRequestException(e.getMessage());
		}
		
		return Response.ok(resource)
				.build(); 
	}
	
	@GET
	@Path("salesforce/{id}/service/{key}/connection/{environment}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response testConnection(
			@PathParam(value="id") String id,
			@PathParam(value="key") String key,
			@PathParam(value="environment") String environmentName) {
		
		String subject = securityContext.getUserPrincipal().getName();
		
		SalesforceConnectorDTO resource = null;
		try {
			resource = salesforceConnectorService.testConnection(subject, id, key, environmentName);
		} catch (IllegalArgumentException e) {
			throw new BadRequestException(e.getMessage());
		}
		
		return Response.ok(resource)
				.build(); 
		
	}
	
	@GET
	@Path("salesforce/{id}/service/{key}/sobjects")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getSobjects(
			@PathParam(value="id") String id,
			@PathParam(value="key") String key) {
		
		String subject = securityContext.getUserPrincipal().getName();
		
		SalesforceConnectorDTO resource = null;
		try {
			resource = salesforceConnectorService.describeGlobal(subject, id, key);
		} catch (IllegalArgumentException e) {
			throw new BadRequestException(e.getMessage());
		}
		
		return Response.ok(resource)
				.build(); 
	}
	
	@GET
	@Path("salesforce/{id}/service/{key}/query")
	@Produces(MediaType.APPLICATION_JSON)
	public Response query(
			@PathParam(value="id") String id,
			@PathParam(value="key") String key,
			@QueryParam(value="q") String queryString) {
		
		String subject = securityContext.getUserPrincipal().getName();
		
		SObject[] resource = null;
		try {
			resource = salesforceConnectorService.query(subject, id, key, queryString);
		} catch (IllegalArgumentException e) {
			throw new BadRequestException(e.getMessage());
		}
		
		return Response.ok(resource)
				.build(); 
	}
	
	@GET
	@Path("salesforce/{id}/service/{key}/sobjects/{sobject}/fields")
	@Produces(MediaType.APPLICATION_JSON)
	public Response describeSObjects (
			@PathParam(value="id") String id,
			@PathParam(value="key") String key,
			@PathParam(value="sobject") String sobject) {
		
		String subject = securityContext.getUserPrincipal().getName();
		
		Field[] resource = null;
		try {
			resource = salesforceConnectorService.describeSobject(subject, id, key, sobject);
		} catch (IllegalArgumentException e) {
			throw new BadRequestException(e.getMessage());
		}
		
		return Response.ok(resource)
				.build(); 
	}
	
	@DELETE
	@Path("salesforce/{id}/service/{key}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response removeServiceInstance(
			@PathParam(value="id") String id,
			@PathParam(value="key") String key) {
		
		String subject = securityContext.getUserPrincipal().getName();
		
		SalesforceConnectorDTO resource = salesforceConnectorService.removeServiceInstance(subject, id, key);
		
		return Response.ok()
				.entity(resource)
				.build(); 
		
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
					.path("salesforce")
					.path("profilephoto")
					.path("{id}")
					.build(key);
			
			return uri.toString();
			
		} catch (IOException e) {
			throw new WebApplicationException(e, Status.INTERNAL_SERVER_ERROR);
		}
	}
}