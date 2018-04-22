package com.nowellpoint.console.service;

import javax.ws.rs.core.UriBuilder;

import org.bson.types.ObjectId;
import org.mongodb.morphia.query.Query;

import com.nowellpoint.console.model.Address;
import com.nowellpoint.console.model.Identity;
import com.nowellpoint.console.model.Meta;
import com.nowellpoint.console.model.Organization;
import com.nowellpoint.console.model.Resources;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nowellpoint.api.OrganizationResource;
import com.nowellpoint.client.resource.UserProfileResource;
import com.nowellpoint.console.entity.IdentityDAO;

public class IdentityService extends AbstractService {
	
	private IdentityDAO identityDAO;
	
	public IdentityService() {
		identityDAO = new IdentityDAO(com.nowellpoint.console.entity.Identity.class, datastore);
	}

	public Identity getIdentity(String id) {
		
		com.nowellpoint.console.entity.Identity document = identityDAO.get(new ObjectId(id));
		
		return fromDocument(document);
	}
	
	public Identity getBySubject(String subject) {
		
		Query<com.nowellpoint.console.entity.Identity> query = identityDAO.createQuery()
				.field("subject")
				.equal(subject);
		
		com.nowellpoint.console.entity.Identity document = identityDAO.findOne(query);
		
		return fromDocument(document);
		
	}
	
	public static Identity fromDocument(com.nowellpoint.console.entity.Identity document) {
		
		String organizationHref = UriBuilder.fromUri("https://localhost:8443")
				.path(OrganizationResource.class)
				.build()
				.toString();
		
		String jobsHref = UriBuilder.fromUri("https://localhost:8443")
				//.path(JobResource.class)
				.build()
				.toString();
		
		String connectorsHref = UriBuilder.fromUri("https://localhost:8443")
				//.path(ConnectorResource.class)
				.build()
				.toString();
		
		Identity identity = Identity.builder()
				.id(document.getUserProfile().getId().toString())
				.userId(document.getUserProfile().getId().toString())
				.email(document.getUserProfile().getEmail())
				.firstName(document.getUserProfile().getFirstName())
				.lastName(document.getUserProfile().getLastName())
				.name(document.getUserProfile().getName())
				.locale(document.getUserProfile().getLocale())
				.timeZone(document.getUserProfile().getTimeZone())
				.resources(Resources.builder()
						.connectors(connectorsHref)
						.organization(organizationHref)
						.jobs(jobsHref)
						.build())
				.meta(Meta.builder()
						.id(document.getUserProfile().getId().toString())
						.resourceClass(UserProfileResource.class)
						.build())
				.address(Address.builder()
						.addedOn(document.getUserProfile().getAddress().getAddedOn())
						.city(document.getUserProfile().getAddress().getCity())
						.countryCode(document.getUserProfile().getAddress().getCountryCode())
						.id(document.getUserProfile().getAddress().getId())
						.latitude(document.getUserProfile().getAddress().getLatitude())
						.longitude(document.getUserProfile().getAddress().getLongitude())
						.postalCode(document.getUserProfile().getAddress().getPostalCode())
						.state(document.getUserProfile().getAddress().getState())
						.street(document.getUserProfile().getAddress().getStreet())
						.updatedOn(document.getUserProfile().getAddress().getUpdatedOn())
						.build())
				.organization(Organization.builder()
						.domain(document.getOrganization().getDomain())
						.id(document.getOrganization().getId().toString())
						.name(document.getOrganization().getName())
						.number(document.getOrganization().getNumber())
						//.subscription(document.getOrganization().)
						//.transactions(elements)
						//.users(elements)
						.build())
				.build();
		
		return identity;
	}
}