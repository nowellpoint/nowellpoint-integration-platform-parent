package com.nowellpoint.api.resource;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;

import javax.annotation.security.PermitAll;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.mongodb.client.FindIterable;
import com.nowellpoint.api.model.document.IsoCountry;
import com.nowellpoint.api.model.domain.IsoCountryList;
import com.nowellpoint.mongodb.document.MongoDatastore;

@Path("iso-countries")
public class IsoCountryResource {
	
	@GET
    @Produces(MediaType.APPLICATION_JSON)
	@PermitAll
    public Response findAll() {
		
		String collectionName = MongoDatastore.getCollectionName(IsoCountry.class);
		
		FindIterable<IsoCountry> documents = MongoDatastore.getDatabase()
				.getCollection(collectionName)
				.withDocumentClass(IsoCountry.class)
				.find();
		
		IsoCountryList isoCountries = new IsoCountryList(documents);
		
		return Response.ok(isoCountries).build();
    }
	
	@GET
	@Path("{language}")
    @Produces(MediaType.APPLICATION_JSON)
	@PermitAll
    public Response findByLanguage(@PathParam("language") String language) {
		
		String collectionName = MongoDatastore.getCollectionName(IsoCountry.class);
		
		FindIterable<IsoCountry> documents = MongoDatastore.getDatabase()
				.getCollection(collectionName)
				.withDocumentClass(IsoCountry.class)
				.find( eq ( "language", language ) );
		
		IsoCountryList isoCountries = new IsoCountryList(documents);
		
		return Response.ok(isoCountries).build();
    }
	
	@GET
	@Path("{language}/{code}")
	@Produces(MediaType.APPLICATION_JSON)
	@PermitAll
	public Response findByIsoCode(@PathParam("language") String language, @PathParam("code") String code) {
		
		String collectionName = MongoDatastore.getCollectionName(IsoCountry.class);
		
		IsoCountry document = MongoDatastore.getDatabase()
				.getCollection(collectionName)
				.withDocumentClass(IsoCountry.class)
				.find( and ( eq ( "language", language ), eq ( "code", code ) ) )
				.first();
		
		com.nowellpoint.api.model.domain.IsoCountry isoCountry = new com.nowellpoint.api.model.domain.IsoCountry(document);
		
		return Response.ok(isoCountry).build();
	}
}