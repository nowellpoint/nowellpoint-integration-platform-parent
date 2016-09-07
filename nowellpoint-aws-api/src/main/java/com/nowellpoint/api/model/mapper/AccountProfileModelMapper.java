package com.nowellpoint.api.model.mapper;

import static com.mongodb.client.model.Filters.eq;

import com.nowellpoint.api.model.dto.AccountProfile;
import com.nowellpoint.api.model.dto.Id;

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
	
	public AccountProfile findAccountProfile(Id id) {
		com.nowellpoint.api.model.document.AccountProfile document = findById(id.getValue());
		return modelMapper.map(document, AccountProfile.class);
	}	
	
	/**
	 * 
	 * 
	 * @param accountProfile
	 * 
	 * 
	 */
	
	public void createAccountProfile(AccountProfile accountProfile) {
		com.nowellpoint.api.model.document.AccountProfile document = modelMapper.map(accountProfile, com.nowellpoint.api.model.document.AccountProfile.class);
		create(getSubject(), document);
		modelMapper.map(document, accountProfile);
	}
	
	/**
	 * 
	 * 
	 * @param accountProfile
	 * 
	 * 
	 */
	
	public void updateAccountProfile(AccountProfile accountProfile) {
		com.nowellpoint.api.model.document.AccountProfile document = modelMapper.map(accountProfile, com.nowellpoint.api.model.document.AccountProfile.class);
		replace(getSubject(), document);
		modelMapper.map(document, accountProfile);
	}
	
	/**
	 * 
	 * 
	 * @param accountProfile
	 * 
	 * 
	 */
	
	public void deleteApplication(AccountProfile accountProfile) {
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
	
	public AccountProfile findAccountProfileByHref(String href) {
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
	
	public AccountProfile findAccountProfileByUsername(String username) {
		com.nowellpoint.api.model.document.AccountProfile document = findOne( eq ( "username", username ) );
		AccountProfile accountProfile = modelMapper.map(document, AccountProfile.class);
		return accountProfile; 
	}
}