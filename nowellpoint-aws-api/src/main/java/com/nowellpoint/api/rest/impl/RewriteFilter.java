package com.nowellpoint.api.rest.impl;

import java.io.IOException;
import java.net.URI;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.ext.Provider;

@Provider
@PreMatching
public class RewriteFilter implements ContainerRequestFilter {

	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {
		URI uri = requestContext.getUriInfo().getRequestUriBuilder().scheme("https").build();
		requestContext.setRequestUri(uri);
	}
}