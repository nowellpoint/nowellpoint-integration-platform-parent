package com.nowellpoint.console.invoice;

import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nowellpoint.console.model.Identity;
import com.nowellpoint.console.model.IdentityRequest;
import com.nowellpoint.console.service.ServiceClient;

public class TestSubscription {
	
	@Test
	public void testSignUp() {
        
        try {
        	
        	IdentityRequest identityRequest = IdentityRequest.builder()
        			.email("john.d.herson@mailinator.com")
        			.firstName("John")
        			.lastName("Herson")
        			.password("Mypassw0rd")
        			.build();
        	
        	Identity identity = ServiceClient.getInstance()
        			.identity()
        			.create(identityRequest);
        	
        	System.out.println(new ObjectMapper().writeValueAsString(identity));
        	
        	System.out.println(identity.getSubject());
        	
        	identity = ServiceClient.getInstance()
        			.identity()
        			.getBySubject(identity.getSubject());
        	
        	identity = ServiceClient.getInstance()
        			.identity()
        			.activate(identity.getId());
        	
        	identity = ServiceClient.getInstance()
        			.identity()
        			.deactivate(identity.getId());
        	
        	ServiceClient.getInstance().identity().delete(identity.getId());
        	
        } catch (com.okta.sdk.resource.ResourceException e) {
        	System.out.println(e.getMessage());
        } catch (JsonProcessingException e) {
			e.printStackTrace();
		}
	}
}