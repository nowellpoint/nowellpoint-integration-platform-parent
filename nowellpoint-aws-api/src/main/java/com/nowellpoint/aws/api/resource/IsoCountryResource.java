package com.nowellpoint.aws.api.resource;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import javax.annotation.security.PermitAll;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.mongodb.client.MongoCollection;
import com.nowellpoint.aws.api.model.IsoCountry;
import com.nowellpoint.aws.data.mongodb.MongoDatastore;

@Path("/iso-countries")
public class IsoCountryResource {
	
	private static final String COLLECTION_NAME = "iso.countries";
	
	@GET
    @Produces(MediaType.APPLICATION_JSON)
	@PermitAll
    public Response findAll() {
			
		MongoCollection<IsoCountry> collection = MongoDatastore.getDatabase()
				.getCollection(COLLECTION_NAME)
				.withDocumentClass(IsoCountry.class);
			
		List<IsoCountry> countries = StreamSupport.stream(collection.find().spliterator(), false)
				.collect(Collectors.toList());
		
		return Response.ok(countries).build();
    }
	
	@GET
	@Path("/{language}")
    @Produces(MediaType.APPLICATION_JSON)
	@PermitAll
    public Response findByLanguage(@PathParam("language") String language) {
		
		MongoCollection<IsoCountry> collection = MongoDatastore.getDatabase()
				.getCollection(COLLECTION_NAME)
				.withDocumentClass(IsoCountry.class);
		
		List<IsoCountry> countries = StreamSupport.stream(collection.find( eq ( "language", language ) ).spliterator(), false)
				.collect(Collectors.toList());
		
		return Response.ok(countries).build();
    }
	
	@GET
	@Path("/{language}/{code}")
	@Produces(MediaType.APPLICATION_JSON)
	@PermitAll
	public Response findByIsoCode(@PathParam("language") String language, @PathParam("code") String code) {
		
		MongoCollection<IsoCountry> collection = MongoDatastore.getDatabase()
				.getCollection(COLLECTION_NAME)
				.withDocumentClass(IsoCountry.class);
				
		IsoCountry country = collection.find( and ( eq ( "language", language ), eq ( "code", code ) ) ).first();
		
		return Response.ok(country).build();
	}
}