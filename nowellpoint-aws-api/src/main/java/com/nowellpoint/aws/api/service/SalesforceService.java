package com.nowellpoint.aws.api.service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;

import org.jboss.logging.Logger;
import org.modelmapper.ModelMapper;

import com.nowellpoint.aws.api.dto.sforce.OrganizationContact;
import com.nowellpoint.aws.api.dto.sforce.OrganizationDTO;
import com.nowellpoint.aws.http.HttpResponse;
import com.nowellpoint.aws.http.RestResource;
import com.nowellpoint.aws.model.sforce.Identity;
import com.nowellpoint.aws.model.sforce.Organization;

public class SalesforceService {
	
	private static final Logger LOGGER = Logger.getLogger(SalesforceService.class);
	
	protected final ModelMapper modelMapper;
	
	public SalesforceService() {
		this.modelMapper = new ModelMapper();
	}

	public OrganizationDTO getOrganizationByTokenId(String bearerToken, String id) {		
		Identity identity = null;
		Organization organization = null;
		
		try {
			
			HttpResponse httpResponse = RestResource.get(id)
					.acceptCharset(StandardCharsets.UTF_8)
					.bearerAuthorization(bearerToken)
					.accept(MediaType.APPLICATION_JSON)
					.queryParameter("version", "latest")
					.execute();
			
			LOGGER.info("Status Code: " + httpResponse.getStatusCode() + " : " + httpResponse.getURL());
	    	
	    	if (httpResponse.getStatusCode() >= 400) {
				throw new WebApplicationException(httpResponse.getAsString(), httpResponse.getStatusCode());
			}
	    	
	    	identity = httpResponse.getEntity(Identity.class);
	    	
	    	final String ORGANIZATION_FIELDS = "Id,Division,Fax,DefaultLocaleSidKey,FiscalYearStartMonth,"
	     			+ "InstanceName,IsSandbox,LanguageLocaleKey,Name,OrganizationType,Phone,PrimaryContact,"
	     			+ "UsesStartDateAsFiscalYearName";
	     	
			httpResponse = RestResource.get(identity.getUrls().getSobjects())
	     			.bearerAuthorization(bearerToken)
	     			.path("Organization")
	     			.path(identity.getOrganizationId())
	     			.queryParameter("fields", ORGANIZATION_FIELDS)
	     			.queryParameter("version", "latest")
	     			.execute();
	     	
			LOGGER.info("Status Code: " + httpResponse.getStatusCode() + " : " + httpResponse.getURL());
			
			if (httpResponse.getStatusCode() >= 400) {
				throw new WebApplicationException(httpResponse.getAsString(), httpResponse.getStatusCode());
			}
	     	
	     	organization = httpResponse.getEntity(Organization.class);
	     	
		} catch (IOException e) {
			LOGGER.error( "getOrganizationByTokenId", e.getCause() );
			throw new WebApplicationException(e, Status.BAD_REQUEST);
		}
		
		OrganizationContact contact = modelMapper.map( identity, OrganizationContact.class );
		
		OrganizationDTO resource = modelMapper.map( organization, OrganizationDTO.class );
		resource.getAttributes().setId(organization.getId());
		resource.setOrganizationContact(contact);
		
		return resource;
	}	
}