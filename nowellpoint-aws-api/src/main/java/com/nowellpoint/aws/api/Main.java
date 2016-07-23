package com.nowellpoint.aws.api;

import java.util.Optional;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.ClassLoaderAsset;
import org.wildfly.swarm.container.Container;
import org.wildfly.swarm.jaxrs.JAXRSArchive;
//import org.wildfly.swarm.swagger.SwaggerArchive;

import org.wildfly.swarm.logging.LoggingFraction;
import org.wildfly.swarm.swagger.SwaggerArchive;

import com.nowellpoint.aws.model.admin.Properties;

public class Main {
	
	public static void main(String[] args) throws Exception {
		
		//
		// set system properties
		//
		
		System.setProperty("swarm.http.port", getPort());

		//
		// build and start the container
		//
		
        Container container = new Container();
        
		//
        // set system properties from configuration
        //

        Properties.setSystemProperties(System.getenv("NCS_PROPERTY_STORE"));
        
        //
        // create the JAX-RS deployment archive
        // 
        
        JAXRSArchive deployment = ShrinkWrap.create(JAXRSArchive.class)
        		.addPackages(true, Package.getPackage("com.nowellpoint.aws.api"))
        		.addAsWebInfResource(new ClassLoaderAsset("WEB-INF/web.xml", Main.class.getClassLoader()), "web.xml")
        		.addAsWebInfResource(new ClassLoaderAsset("META-INF/beans.xml", Main.class.getClassLoader()), "beans.xml")
        		.addAsWebResource(new ClassLoaderAsset("ValidationMessages.properties", Main.class.getClassLoader()), "ValidationMessages.properties");
        
        SwaggerArchive archive = deployment.as(SwaggerArchive.class)
        		.setVersion("1.0")
        		.setTitle("Nowellpoint Cloud Services API")
        		.setContextRoot("/")
        		.setResourcePackages("com.nowellpoint.aws.api.resource");
        
        deployment.addAllDependencies();
        
        //
        // start the container and deploy the archives
        //

        container.fraction(LoggingFraction.createDefaultLoggingFraction()).start().deploy(deployment);
    }
	
	private static String getPort() {
		return Optional.ofNullable(System.getenv().get("PORT")).orElse("9090");
	}
}