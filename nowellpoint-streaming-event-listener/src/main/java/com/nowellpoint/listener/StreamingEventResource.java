package com.nowellpoint.listener;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.Bucket;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

@Path("/")
public class StreamingEventResource {

    @GET
    @Path("/greeting")
    @Produces("application/json")
    public Response greeting(@QueryParam("name") String name) {
        final AmazonS3 s3client = AmazonS3ClientBuilder.defaultClient();

		List<Bucket> buckets = s3client.listBuckets();
		System.out.println("Number of buckets: " + buckets.size());
        System.out.println("Your Amazon S3 buckets are:");
        for (Bucket b : buckets) {
            System.out.println("* " + b.getName());
        }

        return Response.ok().build();
    }
}