package com.nowellpoint.aws.data.test;

import java.sql.Date;
import java.time.Instant;
import java.util.Set;

import org.bson.types.ObjectId;
import org.junit.Assert;
import org.junit.Test;

import com.nowellpoint.mongodb.Datastore;
import com.nowellpoint.mongodb.DocumentManager;
import com.nowellpoint.mongodb.DocumentManagerFactory;
import com.nowellpoint.mongodb.document.DocumentNotFoundException;

public class TestMongoClientAsync {
	
	@Test
	public void testMongoClientConnect() {		
		
		DocumentManagerFactory dmf = Datastore.createDocumentManagerFactory(System.getenv("MONGO_CLIENT_URI"));
		DocumentManager dm = dmf.createDocumentManager();
		
		AccountProfile query = dm.fetch( AccountProfile.class, new ObjectId("5808408e392e00330aeef78d") );
		
		Assert.assertNotNull(query.getPhotos());
		Assert.assertNotNull(query.getIsActive());
		Assert.assertNotNull(query.getTimeZoneSidKey());
		Assert.assertNotNull(query.getCreatedBy());
		Assert.assertNotNull(query.getCreatedBy().getId());
		Assert.assertNotNull(query.getCreatedBy().getName());
		Assert.assertNotNull(query.getCreatedOn());
		Assert.assertNotNull(query.getLastUpdatedBy());
		Assert.assertNotNull(query.getLastUpdatedOn());
		Assert.assertNotNull(query.getAddress());
		Assert.assertNotNull(query.getAddress().getCity());
		Assert.assertNotNull(query.getAddress().getState());
		Assert.assertNotNull(query.getTransactions());
		
		query.getTransactions().stream().forEach( t -> {
			Assert.assertNotNull(t.getCreditCard());
			Assert.assertNotNull(t.getCreditCard().getCardType());
			Assert.assertNotNull(t.getAmount());
		});
		
		UserInfo identity = dm.getReference(UserInfo.class, new ObjectId("5808408e392e00330aeef78d"));
		
		AccountProfile accountProfile = new AccountProfile();
		accountProfile.setName("John Herson");
		accountProfile.setFirstName("John");
		accountProfile.setLastName("Herson");
		accountProfile.setEmail("email@gmail.com");
		accountProfile.setCompany("Nowellpoint");
		accountProfile.setCreatedBy(identity);
		accountProfile.setLastUpdatedBy(identity);
		accountProfile.setAddress(query.getAddress());
		accountProfile.setPhotos(query.getPhotos());
		accountProfile.setCreatedOn(Date.from(Instant.now()));
		accountProfile.setLastUpdatedOn(Date.from(Instant.now()));
		accountProfile.setIsActive(Boolean.TRUE);
		accountProfile.setLocaleSidKey("en_US");
		accountProfile.setTransactions(query.getTransactions());
		
		dm.insertOne( accountProfile );
		
		Assert.assertNotNull(accountProfile.getId());
		Assert.assertNotNull(accountProfile.getCreatedBy().getId());
		Assert.assertNotNull(accountProfile.getPhotos());
		Assert.assertNotNull(accountProfile.getAddress());
		Assert.assertNull(accountProfile.getCreatedBy().getName());
		Assert.assertNull(accountProfile.getLastUpdatedBy().getName());
				
		dm.refresh( identity );
		
		Assert.assertNotNull(identity.getId());
		Assert.assertNotNull(identity.getPhotos());
		Assert.assertNotNull(identity.getAddress());
		Assert.assertNotNull(accountProfile.getCreatedBy().getName());
		Assert.assertNotNull(accountProfile.getLastUpdatedBy().getName());
		
		accountProfile = dm.fetch( AccountProfile.class, accountProfile.getId() );
		
		Assert.assertNotNull(accountProfile.getId() );
		Assert.assertNotNull(accountProfile.getCreatedBy().getId() );
		Assert.assertNotNull(accountProfile.getCreatedBy().getName() );
		Assert.assertNotNull(accountProfile.getPhotos());
		Assert.assertNotNull(accountProfile.getAddress());
		Assert.assertNotNull(accountProfile.getCreatedBy().getAddress());
		
		dm.deleteOne( accountProfile );
		
		try {
			accountProfile = dm.fetch( AccountProfile.class, accountProfile.getId() );
		} catch (DocumentNotFoundException e) {
			System.out.println(e.getMessage());
		}
		
		Set<AccountProfile> accountProfiles = dm.findAll(AccountProfile.class);
		
		Assert.assertNotEquals(accountProfiles.size(), 0);
		
		Set<SalesforceConnector> connectorList = dm.findAll(SalesforceConnector.class);
		
		System.out.println(connectorList.size());
		
		dmf.close();
	}	
}