package com.nowellpoint.api.model.mapper;

import static com.mongodb.client.model.Filters.eq;

import java.util.Optional;

import com.nowellpoint.api.model.document.AccountProfileDocument;
import com.nowellpoint.api.model.dto.AccountProfile;
import com.nowellpoint.api.model.dto.Id;

/**************************************************************************************************************************
 * 
 * 
 * @author jherson
 *
 * 
 *************************************************************************************************************************/

public class AccountProfileModelMapper extends AbstractModelMapper<AccountProfileDocument> {
	
	
	/**************************************************************************************************************************
	 * 
	 * 
	 * constructor
	 * 
	 * 
	 *************************************************************************************************************************/
	
	public AccountProfileModelMapper() {
		super(AccountProfileDocument.class);
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
		AccountProfileDocument document = findById(id.getValue());
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
		AccountProfileDocument document = modelMapper.map(accountProfile, AccountProfileDocument.class);
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
		AccountProfileDocument document = modelMapper.map(accountProfile, AccountProfileDocument.class);
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
		AccountProfileDocument document = modelMapper.map(accountProfile, AccountProfileDocument.class);
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
	
	public AccountProfile findAccountProfileBySubject(String subject) {
		AccountProfileDocument document = findOne( eq ( "href", subject) );
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
		Optional<AccountProfileDocument> document = Optional.ofNullable(findOne( eq( "username", username ) ) );
		AccountProfile accountProfile = null;
		if (document.isPresent()) {
			accountProfile = modelMapper.map(document.get(), AccountProfile.class);
		}
		return accountProfile; 
	}
}