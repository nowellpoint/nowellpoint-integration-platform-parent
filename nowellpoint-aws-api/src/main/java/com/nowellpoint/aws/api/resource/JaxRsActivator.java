package com.nowellpoint.aws.api.resource;

import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

/**
 * @author John Herson
 */

@ApplicationPath("/rest")
public class JaxRsActivator extends Application {
	
	@Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> resources = new java.util.HashSet<>();
        resources.add(HealthCheckResource.class);
        resources.add(SignUpService.class);
        resources.add(TokenResource.class);
        resources.add(IsoCountryResource.class);
        resources.add(IdentityResource.class);
        resources.add(AccountResource.class);
        resources.add(SalesforceResource.class);
        resources.add(ProjectResource.class);
        return resources;
	}
}