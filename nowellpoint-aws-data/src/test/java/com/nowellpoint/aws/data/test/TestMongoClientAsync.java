package com.nowellpoint.aws.data.test;

import java.util.Set;

import org.junit.Test;

import com.nowellpoint.mongodb.Datastore;
import com.nowellpoint.mongodb.DocumentManager;
import com.nowellpoint.mongodb.DocumentManagerFactory;
import com.nowellpoint.util.Properties;

public class TestMongoClientAsync {
	
	@Test
	public void testMongoClientConnect() {
		//Properties.loadProperties(System.getenv("NOWELLPOINT_PROPERTY_STORE"));
		
		System.setProperty("mongo.client.uri", "");
		
		AccountProfile accountProfile = new AccountProfile();
		accountProfile.setName("John Herson");
		accountProfile.setFirstName("John");
		accountProfile.setLastName("Herson");
		accountProfile.setEmail("email@gmail.com");
		accountProfile.setCompany("Nowellpoint");
		
		DocumentManagerFactory dmf = Datastore.createDocumentManagerFactory();
		DocumentManager dm = dmf.createDocumentManager();
		dm.insertOne( accountProfile );
		
		accountProfile = dm.findOne(AccountProfile.class, accountProfile.getId() );
		
		System.out.println( accountProfile.getId() );
		
		dm.deleteOne( accountProfile );
		
		Set<AccountProfile> accountProfiles = dm.findAll(AccountProfile.class);
		System.out.println(accountProfiles.size());
		
		dmf.close();
	}	
}