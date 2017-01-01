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
	 * @param id primary key for the AccountProfile
	 * @return AccountProfile for id
	 */
	
	protected AccountProfile findAccountProfile(String id) {
		com.nowellpoint.api.model.document.AccountProfile document = findById(id);
		return modelMapper.map(document, AccountProfile.class);
	}	
	
	/**
	 * 
	 * @param accountProfile the record to be created
	 */
	
	protected void createAccountProfile(AccountProfile accountProfile) {
		com.nowellpoint.api.model.document.AccountProfile document = modelMapper.map(accountProfile, com.nowellpoint.api.model.document.AccountProfile.class);
		create(document);
		hset(encode(getSubject()), document);
		modelMapper.map(document, accountProfile);
	}
	
	/**
	 * 
	 * @param accountProfile the record to be updated
	 */
	
	protected void updateAccountProfile(AccountProfile accountProfile) {
		com.nowellpoint.api.model.document.AccountProfile document = modelMapper.map(accountProfile, com.nowellpoint.api.model.document.AccountProfile.class);
		replace(document);
		hset(encode(getSubject()), document);
		modelMapper.map(document, accountProfile);
	}
	
	/**
	 * 
	 * @param accountProfile the record the be deleted
	 */
	
	protected void deleteAccountProfile(AccountProfile accountProfile) {
		com.nowellpoint.api.model.document.AccountProfile document = modelMapper.map(accountProfile, com.nowellpoint.api.model.document.AccountProfile.class);
		delete(document);
		hdel(encode(getSubject()), document);
	}
	
	/**
	 * 
	 * @param href the value of href for looking up AccountProfile
	 * @return the AccountProfile that was found
	 */
	
	protected AccountProfile findAccountProfileByHref(String href) {
		com.nowellpoint.api.model.document.AccountProfile document = findOne( eq ( "href", href) );
		AccountProfile accountProfile = modelMapper.map(document, AccountProfile.class);
		return accountProfile;
	}
	
	/**
	 * 
	 * @param username the value of username for looking up AccountProfile
	 * @return the AccountProfile that matches username
	 */
	
	protected AccountProfile findAccountProfileByUsername(String username) {
		com.nowellpoint.api.model.document.AccountProfile document = findOne( eq ( "username", username ) );
		AccountProfile accountProfile = modelMapper.map(document, AccountProfile.class);
		return accountProfile; 
	}
}