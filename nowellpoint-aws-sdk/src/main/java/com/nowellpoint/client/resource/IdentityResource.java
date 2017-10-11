package com.nowellpoint.client.resource;

import com.nowellpoint.client.model.Identity;
import com.nowellpoint.client.model.Token;
import com.nowellpoint.client.model.exception.ForbiddenException;
import com.nowellpoint.client.model.exception.NotFoundException;
import com.nowellpoint.client.model.exception.ServiceUnavailableException;
import com.nowellpoint.http.HttpResponse;
import com.nowellpoint.http.MediaType;
import com.nowellpoint.http.RestResource;
import com.nowellpoint.http.Status;

public class IdentityResource extends AbstractResource {
	
	/**
	 * 
	 * @param token
	 */
	
	public IdentityResource(Token token) {
		super(token);
	}
	
	public Identity get(String id) {
		
		System.out.println(id);
		System.out.println(token.getAccessToken());
		
		HttpResponse httpResponse = RestResource.get(id)
				.bearerAuthorization(token.getAccessToken())
				.accept(MediaType.APPLICATION_JSON)
				.execute();
		
		Identity resource = null;
		
		if (httpResponse.getStatusCode() == Status.OK) {
			resource = httpResponse.getEntity(Identity.class);
		} else if (httpResponse.getStatusCode() == Status.NOT_FOUND) {
			throw new NotFoundException(httpResponse.getAsString());
		} else if (httpResponse.getStatusCode() == Status.FORBIDDEN) {
			throw new ForbiddenException(httpResponse.getAsString());
		} else {
			throw new ServiceUnavailableException(httpResponse.getAsString());
	    }
	    	
    	return resource;
	} 
}