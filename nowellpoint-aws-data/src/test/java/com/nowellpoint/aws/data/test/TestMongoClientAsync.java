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

public class TestMongoClientAsync {
	
	@Test
	public void testMongoClientConnect() {		
		
		DocumentManagerFactory dmf = Datastore.createDocumentManagerFactory("");
		DocumentManager dm = dmf.createDocumentManager();
		
		AccountProfile query = dm.fetch( AccountProfile.class, new ObjectId("5808408e392e00330aeef78d") );
		
		Assert.assertNotNull(query.getPhotos());
		Assert.assertNotNull(query.getIsActive());
		Assert.assertNotNull(query.getTimeZoneSidKey());
		Assert.assertNotNull(query.getCreatedDate());
		Assert.assertNotNull(query.getIdentity().getAddress());
		Assert.assertNotNull(query.getIdentity().getAddress().getCity());
		Assert.assertNotNull(query.getIdentity().getAddress().getState());
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
		accountProfile.setIdentity(identity);
		accountProfile.setAddress(query.getAddress());
		accountProfile.setPhotos(query.getPhotos());
		accountProfile.setCreatedDate(Date.from(Instant.now()));
		accountProfile.setLastModifiedDate(Date.from(Instant.now()));
		accountProfile.setIsActive(Boolean.TRUE);
		accountProfile.setDoubleNumber(new Double(56.00));
		accountProfile.setIntegerNumber(403);
		accountProfile.setLongNumber(new Long(120000));
		accountProfile.setLocaleSidKey("en_US");
		accountProfile.setTransactions(query.getTransactions());
		
		dm.insertOne( accountProfile );
		
		Assert.assertNotNull(accountProfile.getId());
		Assert.assertNotNull(accountProfile.getIdentity().getId());
		Assert.assertNotNull(accountProfile.getPhotos());
		Assert.assertNotNull(accountProfile.getAddress());
				
		dm.refresh( identity );
		
		Assert.assertNotNull(identity.getId());
		Assert.assertNotNull(identity.getPhotos());
		Assert.assertNotNull(identity.getAddress());
		
		accountProfile = dm.fetch( AccountProfile.class, accountProfile.getId() );
		
		Assert.assertNotNull(accountProfile.getId() );
		Assert.assertNotNull(accountProfile.getIdentity().getId() );
		Assert.assertNotNull(accountProfile.getIdentity().getName() );
		Assert.assertNotNull(accountProfile.getPhotos());
		Assert.assertNotNull(accountProfile.getAddress());
		Assert.assertNotNull(accountProfile.getIdentity().getAddress());
		
		//dm.deleteOne( accountProfile );
		
		Set<AccountProfile> accountProfiles = dm.findAll(AccountProfile.class);
		
		Assert.assertNotEquals(accountProfiles.size(), 0);
		
		dmf.close();
	}	
}