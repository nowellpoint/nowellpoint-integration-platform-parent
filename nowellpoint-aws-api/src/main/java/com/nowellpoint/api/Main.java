/**
 * 
 * Copyright 2015-2016 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */

package com.nowellpoint.api;

import java.util.TimeZone;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.ClassLoaderAsset;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.wildfly.swarm.Swarm;
import org.wildfly.swarm.jaxrs.JAXRSArchive;
import org.wildfly.swarm.swagger.SwaggerArchive;

import com.nowellpoint.util.Properties;

public class Main {
	
	public static void main(String[] args) throws Exception {
		
		//
		// build the container
		//
		
        Swarm container = new Swarm(args);
        
		//
        // dynamically set secure system properties based configuration
        //

        Properties.loadProperties(container.configView()
                .resolve("propertyStore.name")
                .getValue());
        
        //
        // set default time zone to UTC
        //
        
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        
        //
        // create the JAX-RS deployment archive
        // 
        
        JAXRSArchive deployment = ShrinkWrap.create(JAXRSArchive.class, "nowellpoint-api.war")
        		.addPackages(true, Package.getPackage("com.nowellpoint.api"))
        		.addAsResource(new ClassLoaderAsset("messages_en_US.properties", Main.class.getClassLoader()), "messages_en_US.properties")
        		.addAsResource(new ClassLoaderAsset("invoice_en_US.properties", Main.class.getClassLoader()), "invoice_en_US.properties")
        		.addAsWebResource(new ClassLoaderAsset("ValidationMessages.properties", Main.class.getClassLoader()), "ValidationMessages.properties")
        		.addAsWebResource(new ClassLoaderAsset("WEB-INF/web.xml", Main.class.getClassLoader()), "web.xml")
        		.addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
        		.addAllDependencies();
        
        //
        // enable swagger
        //
        
        final SwaggerArchive archive = deployment.as(SwaggerArchive.class);
        archive.setResourcePackages("com.nowellpoint.api.rest");
        archive.setPrettyPrint(Boolean.TRUE);

        //
        // start the container and deploy the archive
        //
        
        container.start(deployment);
    }
}