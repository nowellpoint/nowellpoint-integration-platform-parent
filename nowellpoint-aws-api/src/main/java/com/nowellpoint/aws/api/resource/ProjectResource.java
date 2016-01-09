package com.nowellpoint.aws.api.resource;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.mongodb.client.MongoCollection;
import com.nowellpoint.aws.api.data.Datastore;
import com.nowellpoint.aws.model.Project;

@Path("/project")
public class ProjectResource {
	
	private static final String COLLECTION_NAME = "projects";
	
	@GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response findAll() {
		
		MongoCollection<Project> collection = Datastore.getDatabase()
				.getCollection(COLLECTION_NAME)
				.withDocumentClass(Project.class);
		
		System.out.println("records: " + collection.count());
			
		List<Project> projects = StreamSupport.stream(collection.find().spliterator(), false)
					.collect(Collectors.toList());
		
		return Response.ok(projects).build();
    }
}