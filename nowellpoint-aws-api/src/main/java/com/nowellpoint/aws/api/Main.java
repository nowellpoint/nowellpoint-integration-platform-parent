package com.nowellpoint.aws.api;

import java.util.Optional;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.ClassLoaderAsset;
import org.wildfly.swarm.container.Container;
import org.wildfly.swarm.jaxrs.JAXRSArchive;

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
        		.addPackage("com.nowellpoint.aws.api.dto")
        		.addPackage("com.nowellpoint.aws.api.dto.idp")
        		.addPackage("com.nowellpoint.aws.api.dto.sforce")
        		.addPackage("com.nowellpoint.aws.api.resource")
        		.addPackage("com.nowellpoint.aws.api.service")
        		.addPackage("com.nowellpoint.aws.api.util")
        		.addPackage("com.nowellpoint.aws.api.exception")
        		.addAsWebInfResource(new ClassLoaderAsset("WEB-INF/web.xml", Main.class.getClassLoader()), "web.xml")
        		.addAsWebInfResource(new ClassLoaderAsset("META-INF/beans.xml", Main.class.getClassLoader()), "beans.xml")
        		.addAllDependencies();
        
//        SwaggerArchive archive = deployment.as(SwaggerArchive.class)
//        		.setVersion("1.0")
//        		.setTitle("Nowellpoint Cloud Services API")
//        		.setContextRoot("/");
        
//        archive.setResourcePackages("com.nowellpoint.aws.api.resource");
        
        //
        // start the container and deploy the archives
        //
 
        container.start().deploy(deployment);
    }
	
	private static String getPort() {
		return Optional.ofNullable(System.getenv().get("PORT")).orElse("9090");
	}
}