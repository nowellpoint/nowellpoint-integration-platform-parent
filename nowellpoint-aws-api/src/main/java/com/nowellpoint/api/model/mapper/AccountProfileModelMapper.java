package com.nowellpoint.api.model.mapper;

import static com.mongodb.client.model.Filters.eq;

import com.nowellpoint.api.model.domain.AccountProfile;

/**
 * 
 * 
 * @author jherson
 *
 * 
 */

public class AccountProfileModelMapper extends AbstractModelMapper<com.nowellpoint.api.model.document.AccountProfile> {
	
	/**
	 * 
	 */
	
	public AccountProfileModelMapper() {
		super(com.nowellpoint.api.model.document.AccountProfile.class);
	}	
	
	/**
	 * 
	 */
	
	protected void createAccountProfile(AccountProfile accountProfile) {
		com.nowellpoint.api.model.document.AccountProfile document = accountProfile.toDocument(com.nowellpoint.api.model.document.AccountProfile.class);
		create(document);
		hset(encode(getSubject()), document);
		modelMapper.map(document, accountProfile);
	}
	
	/**
	 * 
	 */
	
	protected void updateAccountProfile(AccountProfile accountProfile) {
		com.nowellpoint.api.model.document.AccountProfile document = accountProfile.toDocument(com.nowellpoint.api.model.document.AccountProfile.class);
		replace(document);
		hset(encode(getSubject()), document);
		modelMapper.map(document, accountProfile);
	}
	
	/**
	 * 
	 */
	
	protected void deleteAccountProfile(AccountProfile accountProfile) {
		com.nowellpoint.api.model.document.AccountProfile document = accountProfile.toDocument(com.nowellpoint.api.model.document.AccountProfile.class);
		delete(document);
		hdel(encode(getSubject()), document);
	}
	
	/**
	 * 
	 */
	
	protected AccountProfile findByAccountHref(String accountHref) {
		com.nowellpoint.api.model.document.AccountProfile document = findOne( eq ( "accountHref", accountHref) );
		AccountProfile accountProfile = modelMapper.map(document, AccountProfile.class);
		return accountProfile;
	}
	
	/**
	 * 
	 */
	
	protected AccountProfile findAccountProfileByUsername(String username) {
		com.nowellpoint.api.model.document.AccountProfile document = findOne( eq ( "username", username ) );
		AccountProfile accountProfile = modelMapper.map(document, AccountProfile.class);
		return accountProfile; 
	}
	
	/**
	 * 
	 */
	
	protected AccountProfile findBySubscriptionId(String subscriptionId) {
		com.nowellpoint.api.model.document.AccountProfile document = findOne( eq ( "subscription.subscriptionId", subscriptionId ) );
		AccountProfile accountProfile = modelMapper.map(document, AccountProfile.class);
		return accountProfile;
	}
}