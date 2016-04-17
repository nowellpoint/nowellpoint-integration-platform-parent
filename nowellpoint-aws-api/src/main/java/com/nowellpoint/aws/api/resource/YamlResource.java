package com.nowellpoint.aws.api.resource;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.UUID;

import javax.net.ssl.HttpsURLConnection;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.Response.Status;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.util.IOUtils;
import com.esotericsoftware.yamlbeans.YamlWriter;

@Path("/yaml")
public class YamlResource {

	
	@POST
	@Path("/{key}")
	public Response createConfiguration(@PathParam("key") String key, Map<String,Object> configParams) {
		
		AmazonS3 s3Client = new AmazonS3Client();
		
		try {
			YamlWriter writer = new YamlWriter(new StringWriter());
			writer.write(configParams);
			writer.close();
			
			ObjectMetadata objectMetadata = new ObjectMetadata();
	    	objectMetadata.setContentLength(writer.toString().length());
	    	objectMetadata.setContentType(MediaType.TEXT_PLAIN);
			
	    	PutObjectRequest putObjectRequest = new PutObjectRequest("nowellpoint-configuration-files", key, new ByteArrayInputStream(writer.toString().getBytes(StandardCharsets.UTF_8)), objectMetadata);
	    	
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