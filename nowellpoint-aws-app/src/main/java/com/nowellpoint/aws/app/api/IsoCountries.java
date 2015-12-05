package com.nowellpoint.aws.app.api;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.mongodb.client.MongoCollection;
import com.nowellpoint.aws.app.data.Datastore;
import com.nowellpoint.aws.model.IsoCountry;

@Path("/iso-countries")
public class IsoCountries {
	
	@GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response findAll() {
		
		MongoCollection<IsoCountry> collection = Datastore.getDatabase()
				.getCollection("iso.countries")
				.withDocumentClass(IsoCountry.class);
		
		List<IsoCountry> countries = StreamSupport.stream(collection.find().spliterator(), false)
				.collect(Collectors.toList());
		
		return Response.ok(countries).build();
    }
}