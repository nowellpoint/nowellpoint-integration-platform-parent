package com.nowellpoint.aws.api.tasks;

import static com.mongodb.client.model.Filters.eq;

import java.time.Instant;
import java.util.Date;
import java.util.Optional;
import java.util.concurrent.Callable;

import com.nowellpoint.aws.api.model.AccountProfile;
import com.nowellpoint.aws.api.model.Address;
import com.nowellpoint.aws.data.MongoDBDatastore;
import com.nowellpoint.aws.data.annotation.Document;

public class AccountProfileSetupTask implements Callable<AccountProfile> {
	
	private AccountProfileSetupRequest accountProfileSetupRequest;
	
	public AccountProfileSetupTask(AccountProfileSetupRequest accountProfileSetupRequest) {
		this.accountProfileSetupRequest = accountProfileSetupRequest;
	}

	@Override
	public AccountProfile call() throws Exception {
		
		AccountProfile accountProfile = Optional.ofNullable( MongoDBDatastore.getDatabase().getCollection( AccountProfile.class.getAnnotation(Document.class).collectionName() )
				.withDocumentClass( AccountProfile.class )
				.find( eq ( "username", accountProfileSetupRequest.getUsername() ) )
				.first() )
				.orElse(new AccountProfile());
		
		accountProfile.setLastModifiedById(accountProfileSetupRequest.getHref());
		accountProfile.setLastModifiedDate(Date.from(Instant.now()));
		accountProfile.setFirstName(accountProfileSetupRequest.getFirstName());
		accountProfile.setLastName(accountProfileSetupRequest.getLastName());
		accountProfile.setEmail(accountProfileSetupRequest.getEmail());
		accountProfile.setUsername(accountProfileSetupRequest.getUsername());
		accountProfile.setIsActive(accountProfileSetupRequest.getIsActive());
		
		Address address = accountProfile.getAddress() != null ? accountProfile.getAddress() : new Address();
		address.setCountryCode(accountProfileSetupRequest.getCountryCode());
		
		accountProfile.setAddress(address);
		
		if (accountProfile.getId() != null) {			
			MongoDBDatastore.replaceOne( accountProfile );
		} else {
			accountProfile.setCreatedById(accountProfile.getLastModifiedById());
			accountProfile.setCreatedDate(accountProfile.getLastModifiedDate());
			MongoDBDatastore.insertOne( accountProfile );
		}
		
		return accountProfile;
	}
}