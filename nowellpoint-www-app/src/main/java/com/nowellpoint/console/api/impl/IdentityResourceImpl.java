package com.nowellpoint.console.api.impl;

import javax.ws.rs.core.Response;

import com.nowellpoint.console.api.IdentityResource;
import com.nowellpoint.console.model.Identity;
import com.nowellpoint.console.service.ServiceClient;

public class IdentityResourceImpl implements IdentityResource {

	@Override
	public Response getIdentity(String id) {
		
		Identity identity = ServiceClient.getInstance()
				.identity()
				.get(id);
		
		return Response.ok(identity)
				.build();
	}
}