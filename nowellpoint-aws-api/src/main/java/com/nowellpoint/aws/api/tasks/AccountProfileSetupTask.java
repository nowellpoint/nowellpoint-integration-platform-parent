package com.nowellpoint.aws.api.tasks;

import static com.mongodb.client.model.Filters.eq;

import java.time.Instant;
import java.util.Date;
import java.util.Optional;
import java.util.concurrent.Callable;

import org.jboss.logging.Logger;

import com.mongodb.MongoException;
import com.nowellpoint.aws.api.model.AccountProfile;
import com.nowellpoint.aws.data.MongoDBDatastore;

public class AccountProfileSetupTask implements Callable<String> {
	
	private static final Logger LOGGER = Logger.getLogger(AccountProfileSetupTask.class);
	
	private AccountProfile accountProfile;
	
	public AccountProfileSetupTask(AccountProfile accountProfile) {
		this.accountProfile = accountProfile;
	}

	@Override
	public String call() throws Exception {
		
		String collectionName = AccountProfile.class.getAnnotation(com.nowellpoint.aws.data.annotation.Document.class).collectionName();
		
		Optional<AccountProfile> queryResult = Optional.ofNullable( MongoDBDatastore.getDatabase().getCollection( collectionName )
				.withDocumentClass( AccountProfile.class )
				.find( eq( "username", accountProfile.getUsername() ) )
				.first() );
		
		if (queryResult.isPresent()) {
			accountProfile.setCreatedById(queryResult.get().getCreatedById());
			accountProfile.setCreatedDate(queryResult.get().getCreatedDate());
			accountProfile.setLastModifiedDate(Date.from(Instant.now()));
			
			try {
				MongoDBDatastore.replaceOne( accountProfile );
			} catch (MongoException e) {
				LOGGER.error( "Update Document exception", e.getCause());
				throw new Exception(e.getMessage());
			}
		} else {	
			Date now = Date.from(Instant.now());
			accountProfile.setCreatedDate(now);
			accountProfile.setLastModifiedDate(now);
			
			try {
				MongoDBDatastore.insertOne( accountProfile );
			} catch (MongoException e) {
				LOGGER.error( "Create Document exception", e.getCause());
				throw new Exception(e.getMessage());
			}
		}
		
		return accountProfile.getId().toString();
	}
}