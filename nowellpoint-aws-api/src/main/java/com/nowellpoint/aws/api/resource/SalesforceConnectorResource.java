package com.nowellpoint.aws.api.resource;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.inject.Inject;
import javax.net.ssl.HttpsURLConnection;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import org.hibernate.validator.constraints.NotEmpty;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.DeleteObjectsRequest;
import com.amazonaws.services.s3.model.DeleteObjectsRequest.KeyVersion;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.nowellpoint.aws.api.dto.EnvironmentDTO;
import com.nowellpoint.aws.api.dto.EventListenerDTO;
import com.nowellpoint.aws.api.dto.SalesforceConnectorDTO;
import com.nowellpoint.aws.api.dto.ServiceInstanceDTO;
import com.nowellpoint.aws.api.model.Targets;
import com.nowellpoint.aws.api.service.SalesforceConnectorService;
import com.nowellpoint.aws.model.admin.Properties;
import com.nowellpoint.client.sforce.model.Token;

@Path("connectors")
public class SalesforceConnectorResource {
	
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
	
	@POST
	@Path("salesforce")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public Response createSalesforceConnector(@FormParam("id") @NotEmpty(message = "Missing Token Id") String id,
			@FormParam("instanceUrl") @NotEmpty(message = "Missing Instance Url") String instanceUrl,
			@FormParam("accessToken") @NotEmpty(message = "Missing Access Token") String accessToken,
			@FormParam("refreshToken") @NotEmpty(message = "Missing RefreshToken") String refreshToken) {
		
		Token token = new Token();
		token.setId(id);
		token.setInstanceUrl(instanceUrl);
		token.setAccessToken(accessToken);
		token.setRefreshToken(refreshToken);
		
		SalesforceConnectorDTO resource = salesforceConnectorService.createSalesforceConnector(token);
		
		URI uri = UriBuilder.fromUri(uriInfo.getBaseUri())
				.path(SalesforceResource.class)
				.path("/{id}")
				.build(resource.getId());
		
		return Response.created(uri)
				.entity(resource)
				.build(); 
	}
	
	@GET
	@Path("salesforce/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getSalesforceConnector(@PathParam(value="id") String id) {		
		SalesforceConnectorDTO resource = salesforceConnectorService.findSalesforceConnector(id);
		
		return Response.ok(resource).build();
	}
	
	@POST
	@Path("salesforce/{id}")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateSalesforceConnector(@PathParam(value="id") String id, @FormParam(value="tag") String tag) {		
		SalesforceConnectorDTO resource = salesforceConnectorService.findSalesforceConnector(id);
		resource.setTag(tag);
		
		salesforceConnectorService.updateSalesforceConnector(resource);
		
		return Response.ok()
				.entity(resource)
				.build(); 
	}
	
	@DELETE
	@Path("salesforce/{id}")
	public Response deleteSalesforceConnector(@PathParam(value="id") String id) {		
		SalesforceConnectorDTO resource = salesforceConnectorService.findSalesforceConnector(id);

		AmazonS3 s3Client = new AmazonS3Client();

		List<KeyVersion> keys = new ArrayList<KeyVersion>();
		keys.add(new KeyVersion(resource.getIdentity().getPhotos().getPicture().substring(resource.getIdentity().getPhotos().getPicture().lastIndexOf("/") + 1)));
		keys.add(new KeyVersion(resource.getIdentity().getPhotos().getThumbnail().substring(resource.getIdentity().getPhotos().getThumbnail().lastIndexOf("/") + 1)));

		DeleteObjectsRequest deleteObjectsRequest = new DeleteObjectsRequest("nowellpoint-profile-photos").withKeys(keys);

		s3Client.deleteObjects(deleteObjectsRequest);

		salesforceConnectorService.deleteSalesforceConnector(id);

		return Response.noContent()
				.build(); 
	}
	
	@GET
	@Path("salesforce/{id}/environment/{key}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getEnvironment(@PathParam(value="id") String id, @PathParam(value="key") String key) {		
		
		EnvironmentDTO resource = salesforceConnectorService.getEnvironment(id, key);
		
		if (resource == null) {
			throw new NotFoundException(String.format("Environment for key %s was not found",key));
		}
		
		return Response.ok()
				.entity(resource)
				.build(); 
	}

	@POST
	@Path("salesforce/{id}/environment")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response addEnvironment(@PathParam(value="id") String id, EnvironmentDTO environment) {
		
		EnvironmentDTO resource = salesforceConnectorService.addEnvironment(id, environment);
		
		return Response.ok()
				.entity(resource)
				.build(); 
	}
	
	@POST
	@Path("salesforce/{id}/environment/{key}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateEnvironment(@PathParam(value="id") String id, @PathParam(value="key") String key, EnvironmentDTO environment) {
		
		EnvironmentDTO resource = salesforceConnectorService.updateEnvironment(id, key, environment);
		
		return Response.ok()
				.entity(resource)
				.build(); 
	}
	
	@DELETE
	@Path("salesforce/{id}/environment/{key}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response removeEnvironment(@PathParam(value="id") String id, @PathParam(value="key") String key) {
		
		salesforceConnectorService.removeEnvironment(id, key);
		
		return Response.ok()
				.build(); 
	}
	
	//***
	
	@POST
	@Path("salesforce/{id}/providers/{serviceProviderId}/service/{serviceType}/plan/{code}")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public Response addServiceInstance(
			@PathParam(value="id") String id, 
			@PathParam(value="serviceProviderId") String serviceProviderId, 
			@PathParam(value="serviceType") String serviceType, 
			@PathParam(value="code") String code) {
		
		SalesforceConnectorDTO resource = salesforceConnectorService.addServiceInstance(id, serviceProviderId, serviceType, code);
		
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
		
		SalesforceConnectorDTO resource = salesforceConnectorService.updateServiceInstance(id, key, serviceInstance);
		
		return Response.ok()
				.entity(resource)
				.build(); 	
	}
	
//	@POST
//	@Path("salesforce/{id}/service/{key}/environments")
//	@Consumes(MediaType.APPLICATION_JSON)
//	@Produces(MediaType.APPLICATION_JSON)
//	public Response addEnvironments(
//			@PathParam(value="id") String id,
//			@PathParam(value="key") String key,
//			Set<EnvironmentDTO> environments) {
//		
//		String subject = securityContext.getUserPrincipal().getName();
//		
//		SalesforceConnectorDTO resource = null; //salesforceConnectorService.addEnvironments(subject, id, key, environments);
//		
//		return Response.ok(resource)
//				.build(); 
//	}
//	
//	@POST
//	@Path("salesforce/{id}/service/{key}/variables/{environment}")
//	@Consumes(MediaType.APPLICATION_JSON)
//	@Produces(MediaType.APPLICATION_JSON)
//	public Response addEnvironmentVariables(
//			@PathParam(value="id") String id,
//			@PathParam(value="key") String key,
//			@PathParam(value="environment") String environmentName,
//			Set<EnvironmentVariableDTO> environmentVariables) {
//		
//		String subject = securityContext.getUserPrincipal().getName();
//		
//		SalesforceConnectorDTO resource = null;
//		try {
//			//resource = salesforceConnectorService.addEnvironmentVariables(subject, id, key, environmentName, environmentVariables);
//		} catch (UnsupportedOperationException | IllegalArgumentException e) {
//			throw new BadRequestException(e.getMessage());
//		}
//		
//		return Response.ok(resource)
//				.build(); 
//		
//	}
//	
//	@POST
//	@Path("salesforce/{id}/service/{key}/deployment/{environment}")
//	@Consumes(MediaType.APPLICATION_JSON)
//	@Produces(MediaType.APPLICATION_JSON)
//	public Response deploy(
//			@PathParam(value="id") String id,
//			@PathParam(value="key") String key,
//			@PathParam(value="environment") String environmentName) {
//		
//		String subject = securityContext.getUserPrincipal().getName();
//		
//		SalesforceConnectorDTO resource = null;
//		try {
//			//resource = salesforceConnectorService.deploy(subject, id, key, environmentName);
//		} catch (Exception e) {
//			e.printStackTrace();
//			throw new BadRequestException(e.getMessage());
//		}
//		
//		return Response.ok(resource)
//				.build(); 
//		
//	}
	
	@POST
	@Path("salesforce/{id}/service/{key}/listeners")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response addEventListeners(
			@PathParam(value="id") String id,
			@PathParam(value="key") String key,
			Set<EventListenerDTO> eventListeners) {
		
		SalesforceConnectorDTO resource = null;
		try {
			resource = salesforceConnectorService.addEventListeners(id, key, eventListeners);
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
	public Response addTargets(@PathParam(value="id") String id, @PathParam(value="key") String key, Targets targets) {
				
		SalesforceConnectorDTO resource = null;
		try {
			resource = salesforceConnectorService.addTargets(id, key, targets);
		} catch (UnsupportedOperationException | IllegalArgumentException e) {
			throw new BadRequestException(e.getMessage());
		}
		
		return Response.ok(resource)
				.build(); 
	}
	
//	@GET
//	@Path("salesforce/{id}/service/{key}/connection/{environment}")
//	@Produces(MediaType.APPLICATION_JSON)
//	public Response testConnection(
//			@PathParam(value="id") String id,
//			@PathParam(value="key") String key,
//			@PathParam(value="environment") String environmentName) {
//		
//		String subject = securityContext.getUserPrincipal().getName();
//		
//		SalesforceConnectorDTO resource = null;
//		try {
//			//resource = salesforceConnectorService.testConnection(subject, id, key, environmentName);
//		} catch (IllegalArgumentException e) {
//			throw new BadRequestException(e.getMessage());
//		}
//		
//		return Response.ok(resource)
//				.build(); 
//		
//	}
//	
//	@GET
//	@Path("salesforce/{id}/service/{key}/sobjects")
//	@Produces(MediaType.APPLICATION_JSON)
//	public Response getSobjects(
//			@PathParam(value="id") String id,
//			@PathParam(value="key") String key) {
//		
//		String subject = securityContext.getUserPrincipal().getName();
//		
//		SalesforceConnectorDTO resource = null;
//		try {
//			//resource = salesforceConnectorService.describeGlobal(subject, id, key);
//		} catch (IllegalArgumentException e) {
//			throw new BadRequestException(e.getMessage());
//		}
//		
//		return Response.ok(resource)
//				.build(); 
//	}
	
//	@GET
//	@Path("salesforce/{id}/service/{key}/query")
//	@Produces(MediaType.APPLICATION_JSON)
//	public Response query(
//			@PathParam(value="id") String id,
//			@PathParam(value="key") String key,
//			@QueryParam(value="q") String queryString) {
//		
//		String subject = securityContext.getUserPrincipal().getName();
//		
//		SObject[] resource = null;
//		try {
//			//resource = salesforceConnectorService.query(subject, id, key, queryString);
//		} catch (IllegalArgumentException e) {
//			throw new BadRequestException(e.getMessage());
//		}
//		
//		return Response.ok(resource)
//				.build(); 
//	}
//	
//	@GET
//	@Path("salesforce/{id}/service/{key}/sobjects/{sobject}/fields")
//	@Produces(MediaType.APPLICATION_JSON)
//	public Response describeSObjects (
//			@PathParam(value="id") String id,
//			@PathParam(value="key") String key,
//			@PathParam(value="sobject") String sobject) {
//		
//		String subject = securityContext.getUserPrincipal().getName();
//		
//		Field[] resource = null;
//		try {
//			//resource = salesforceConnectorService.describeSobject(subject, id, key, sobject);
//		} catch (IllegalArgumentException e) {
//			throw new BadRequestException(e.getMessage());
//		}
//		
//		return Response.ok(resource)
//				.build(); 
//	}
	
	@DELETE
	@Path("salesforce/{id}/service/{key}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response removeServiceInstance(@PathParam(value="id") String id, @PathParam(value="key") String key) {
		
		SalesforceConnectorDTO resource = salesforceConnectorService.removeServiceInstance(id, key);
		
		return Response.ok()
				.entity(resource)
				.build(); 
		
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
	    	
	    	String key = UUID.randomUUID().toString().replace("-", "");
			
	    	PutObjectRequest putObjectRequest = new PutObjectRequest("nowellpoint-profile-photos", key, connection.getInputStream(), objectMetadata);
	    	
	    	s3Client.putObject(putObjectRequest);
	    	
	    	URI uri = UriBuilder.fromUri(System.getProperty(Properties.CLOUDFRONT_HOSTNAME))
					.path("{id}")
					.build(key);
			
			return uri.toString();
			
		} catch (IOException e) {
			throw new WebApplicationException(e, Status.INTERNAL_SERVER_ERROR);
		}
	}
}