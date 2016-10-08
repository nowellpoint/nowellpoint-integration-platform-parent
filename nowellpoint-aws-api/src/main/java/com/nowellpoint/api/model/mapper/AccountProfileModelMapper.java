package com.nowellpoint.api.model.mapper;

import static com.mongodb.client.model.Filters.eq;

import com.nowellpoint.api.model.dto.AccountProfile;

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
	 * 
	 * constructor
	 * 
	 * 
	 */
	
	public AccountProfileModelMapper() {
		super(com.nowellpoint.api.model.document.AccountProfile.class);
	}
	
	/**
	 * 
	 * 
	 * @param id
	 * @return
	 * 
	 * 
	 */
	
	protected AccountProfile findAccountProfile(String id) {
		com.nowellpoint.api.model.document.AccountProfile document = findById(id);
		return modelMapper.map(document, AccountProfile.class);
	}	
	
	/**
	 * 
	 * 
	 * @param accountProfile
	 * 
	 * 
	 */
	
	protected void createAccountProfile(AccountProfile accountProfile) {
		com.nowellpoint.api.model.document.AccountProfile document = modelMapper.map(accountProfile, com.nowellpoint.api.model.document.AccountProfile.class);
		create(document);
		hset(encode(getSubject()), document);
		modelMapper.map(document, accountProfile);
	}
	
	/**
	 * 
	 * 
	 * @param accountProfile
	 * 
	 * 
	 */
	
	protected void updateAccountProfile(AccountProfile accountProfile) {
		com.nowellpoint.api.model.document.AccountProfile document = modelMapper.map(accountProfile, com.nowellpoint.api.model.document.AccountProfile.class);
		replace(document);
		hset(encode(getSubject()), document);
		modelMapper.map(document, accountProfile);
	}
	
	/**
	 * 
	 * 
	 * @param accountProfile
	 * 
	 * 
	 */
	
	protected void deleteAccountProfile(AccountProfile accountProfile) {
		com.nowellpoint.api.model.document.AccountProfile document = modelMapper.map(accountProfile, com.nowellpoint.api.model.document.AccountProfile.class);
		delete(getSubject(), document);
	}
	
	/**
	 * 
	 * 
	 * @param subject
	 * @return
	 * 
	 * 
	 */
	
	protected AccountProfile findAccountProfileByHref(String href) {
		com.nowellpoint.api.model.document.AccountProfile document = findOne( eq ( "href", href) );
		AccountProfile accountProfile = modelMapper.map(document, AccountProfile.class);
		return accountProfile;
	}
	
	/**
	 * 
	 * 
	 * @param username
	 * @return
	 * 
	 * 
	 */
	
	protected AccountProfile findAccountProfileByUsername(String username) {
		com.nowellpoint.api.model.document.AccountProfile document = findOne( eq ( "username", username ) );
		AccountProfile accountProfile = modelMapper.map(document, AccountProfile.class);
		return accountProfile; 
	}
}