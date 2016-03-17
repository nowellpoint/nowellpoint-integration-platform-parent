package com.nowellpoint.aws.api.resource;

import java.io.IOException;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.Provider;

import com.nowellpoint.aws.api.util.Subject;
import com.nowellpoint.aws.api.util.SubjectContext;
import com.nowellpoint.aws.model.admin.Properties;
import com.nowellpoint.aws.tools.TokenParser;

@Provider
public class SecurityInterceptor implements ContainerRequestFilter {

	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {
		
		String authorization = requestContext.getHeaderString("Authorization");
		
		if (authorization != null && ! authorization.isEmpty()) {
			String bearerToken = authorization.replaceFirst("Bearer", "").trim();
			
			if (bearerToken == null || bearerToken.isEmpty()) {
				Response response = Response.status(Status.BAD_REQUEST).build();
				requestContext.abortWith(response);
				return;
			}
			
			String subject = TokenParser.getSubject(System.getProperty(Properties.STORMPATH_API_KEY_SECRET), bearerToken);
			
			Subject user = new Subject(subject);
			
			requestContext.setSecurityContext(new SubjectContext(user));
		}
	}
}