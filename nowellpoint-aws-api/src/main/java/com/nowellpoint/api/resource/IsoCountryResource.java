package com.nowellpoint.api.resource;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;

import java.util.Set;

import javax.annotation.security.PermitAll;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.nowellpoint.api.model.document.IsoCountry;
import com.nowellpoint.api.model.domain.IsoCountryList;
import com.nowellpoint.mongodb.DocumentManager;
import com.nowellpoint.mongodb.DocumentManagerFactory;

@Path("iso-countries")
public class IsoCountryResource {
	
	@Inject
	private DocumentManagerFactory documentManagerFactory;
	
	@GET
    @Produces(MediaType.APPLICATION_JSON)
	@PermitAll
    public Response findAll() {
		DocumentManager documentManager = documentManagerFactory.createDocumentManager();
		Set<IsoCountry> documents = documentManager.findAll(IsoCountry.class);
		IsoCountryList isoCountries = new IsoCountryList(documents);
		return Response.ok(isoCountries).build();
    }
	
	@GET
	@Path("{language}")
    @Produces(MediaType.APPLICATION_JSON)
	@PermitAll
    public Response findByLanguage(@PathParam("language") String language) {
		DocumentManager documentManager = documentManagerFactory.createDocumentManager();
		Set<IsoCountry> documents = documentManager.find(IsoCountry.class, eq ( "language", language ));
		IsoCountryList isoCountries = new IsoCountryList(documents);
		return Response.ok(isoCountries).build();
    }
	
	@GET
	@Path("{language}/{code}")
	@Produces(MediaType.APPLICATION_JSON)
	@PermitAll
	public Response findByIsoCode(@PathParam("language") String language, @PathParam("code") String code) {
		DocumentManager documentManager = documentManagerFactory.createDocumentManager();
		IsoCountry document = documentManager.findOne(IsoCountry.class, and ( eq ( "language", language ), eq ( "code", code ) ) );
		com.nowellpoint.api.model.domain.IsoCountry isoCountry = new com.nowellpoint.api.model.domain.IsoCountry(document);
		return Response.ok(isoCountry).build();
	}
}