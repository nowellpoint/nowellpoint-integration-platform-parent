package com.nowellpoint.api.tasks;

import static com.mongodb.client.model.Filters.eq;

import java.time.Instant;
import java.util.Date;
import java.util.Optional;
import java.util.concurrent.Callable;

import com.nowellpoint.api.model.document.AccountProfileDocument;
import com.nowellpoint.api.model.document.Address;
import com.nowellpoint.aws.data.annotation.Document;
import com.nowellpoint.aws.data.mongodb.MongoDatastore;

public class AccountProfileSetupTask implements Callable<AccountProfileDocument> {
	
	private AccountProfileSetupRequest accountProfileSetupRequest;
	
	public AccountProfileSetupTask(AccountProfileSetupRequest accountProfileSetupRequest) {
		this.accountProfileSetupRequest = accountProfileSetupRequest;
	}

	@Override
	public AccountProfileDocument call() throws Exception {
		
		AccountProfileDocument accountProfile = Optional.ofNullable( MongoDatastore.getDatabase().getCollection( AccountProfileDocument.class.getAnnotation(Document.class).collectionName() )
				.withDocumentClass( AccountProfileDocument.class )
				.find( eq ( "username", accountProfileSetupRequest.getUsername() ) )
				.first() )
				.orElse(new AccountProfileDocument());
		
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
			MongoDatastore.replaceOne( accountProfile );
		} else {
			accountProfile.setCreatedById(accountProfile.getLastModifiedById());
			accountProfile.setCreatedDate(accountProfile.getLastModifiedDate());
			MongoDatastore.insertOne( accountProfile );
		}
		
		return accountProfile;
	}
}