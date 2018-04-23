package com.nowellpoint.console.api.impl;

import javax.ws.rs.core.Response;

import com.nowellpoint.console.api.IdentityResource;
import com.nowellpoint.console.model.Identity;
import com.nowellpoint.console.service.IdentityService;

public class IdentityResourceImpl implements IdentityResource {
	
	private IdentityService identityService = new IdentityService();

	@Override
	public Response getIdentity(String id) {
		
		System.out.println("here");
		
		Identity identity = identityService.getIdentity(id);
		
		return Response.ok(identity)
				.build();
	}
}