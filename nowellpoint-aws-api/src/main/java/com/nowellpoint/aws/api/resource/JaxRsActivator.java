package com.nowellpoint.aws.api.resource;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import io.swagger.annotations.Contact;
import io.swagger.annotations.Info;
import io.swagger.annotations.License;
import io.swagger.annotations.SwaggerDefinition;

/**
 * @author John Herson
 */

@SwaggerDefinition(
		basePath = "/rest",
        info = @Info(
                description = "API for all interactions with the Nowellpoint integration platform",
                version = "v1.0",
                title = "The Nowellpoint API",
                termsOfService = "https://www.nowellpoint.com/terms",
                contact = @Contact(
                   name = "John Herson", 
                   email = "john.herson@nowellpoint.com", 
                   url = "https://www.nowellpoint.com"
                ),
                license = @License(
                   name = "Apache 2.0", 
                   url = "http://www.apache.org/licenses/LICENSE-2.0"
                )
        ),
        consumes = {"application/json", "application/x-www-form-urlencoded"},
        produces = {"application/json"},
        schemes = {SwaggerDefinition.Scheme.HTTPS}
)


@ApplicationPath("/rest")
public class JaxRsActivator extends Application {	

}