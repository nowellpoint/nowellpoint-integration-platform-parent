package com.nowellpoint.aws.api.tasks;

import static com.mongodb.client.model.Filters.eq;

import java.time.Instant;
import java.util.Date;
import java.util.Optional;
import java.util.concurrent.Callable;

import org.jboss.logging.Logger;

import com.mongodb.MongoException;
import com.nowellpoint.aws.api.dto.AccountProfileDTO;
import com.nowellpoint.aws.api.model.AccountProfile;
import com.nowellpoint.aws.api.service.AccountProfileService;
import com.nowellpoint.aws.data.MongoDBDatastore;

public class AccountProfileSetupTask implements Callable<AccountProfileDTO> {
	
	private static final Logger LOGGER = Logger.getLogger(AccountProfileSetupTask.class);
	
	private AccountProfileService accountProfileService = new AccountProfileService();
	
	private AccountProfileDTO accountProfile;
	
	public AccountProfileSetupTask(AccountProfileDTO accountProfile) {
		this.accountProfile = accountProfile;
	}

	@Override
	public AccountProfileDTO call() throws Exception {
		
		AccountProfileDTO original = accountProfileService.findAccountProfileByUsername(accountProfile.getUsername());
		
		if (original != null) {
			accountProfile.setId(original.getId());
			accountProfileService.updateAccountProfile(accountProfile);
		} else {
			accountProfileService.createAccountProfile(accountProfile);
		}
		
//		String collectionName = AccountProfile.class.getAnnotation(com.nowellpoint.aws.data.annotation.Document.class).collectionName();
//		
//		Optional<AccountProfile> queryResult = Optional.ofNullable( MongoDBDatastore.getDatabase().getCollection( collectionName )
//				.withDocumentClass( AccountProfile.class )
//				.find( eq( "username", accountProfile.getUsername() ) )
//				.first() );
//		
//		if (queryResult.isPresent()) {
//			accountProfile.setId(queryResult.get().getId());
//			accountProfile.setCreatedById(queryResult.get().getCreatedById());
//			accountProfile.setCreatedDate(queryResult.get().getCreatedDate());
//			accountProfile.setLastModifiedDate(Date.from(Instant.now()));
//			
//			try {
//				MongoDBDatastore.replaceOne( accountProfile );
//			} catch (MongoException e) {
//				LOGGER.error( "Update Document exception", e.getCause());
//				throw new Exception(e.getMessage());
//			}
//		} else {	
//			Date now = Date.from(Instant.now());
//			accountProfile.setCreatedDate(now);
//			accountProfile.setLastModifiedDate(now);
//			
//			try {
//				MongoDBDatastore.insertOne( accountProfile );
//			} catch (MongoException e) {
//				LOGGER.error( "Create Document exception", e.getCause());
//				throw new Exception(e.getMessage());
//			}
//		}
		System.out.println(accountProfile.getId());
		return accountProfile;
	}
}