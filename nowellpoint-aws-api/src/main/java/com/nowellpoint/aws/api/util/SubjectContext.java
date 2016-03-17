package com.nowellpoint.aws.api.util;

import java.security.Principal;

import javax.ws.rs.core.SecurityContext;

public class SubjectContext implements SecurityContext {
	
	private Subject subject;
	
	public SubjectContext(Subject subject) {
		this.subject = subject;
	}

	@Override
	public String getAuthenticationScheme() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Principal getUserPrincipal() {
		return subject;
	}

	@Override
	public boolean isSecure() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isUserInRole(String arg0) {
		// TODO Auto-generated method stub
		return false;
	}
}