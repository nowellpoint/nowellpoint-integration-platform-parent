package com.nowellpoint.api.document.service;

import static com.mongodb.client.model.Filters.eq;

import java.util.Optional;

import com.nowellpoint.api.dto.AccountProfileDTO;
import com.nowellpoint.api.dto.Id;
import com.nowellpoint.api.model.AccountProfile;
import com.nowellpoint.api.service.AbstractModelMapper;

/**************************************************************************************************************************
 * 
 * 
 * @author jherson
 *
 * 
 *************************************************************************************************************************/

public class AccountProfileDocumentService extends AbstractModelMapper<AccountProfile> {
	
	
	/**************************************************************************************************************************
	 * 
	 * 
	 * constructor
	 * 
	 * 
	 *************************************************************************************************************************/
	
	public AccountProfileDocumentService() {
		super(AccountProfile.class);
	}
	
	/**
	 * 
	 * 
	 * @param id
	 * @return
	 * 
	 * 
	 */
	
	public AccountProfileDTO findAccountProfile(Id id) {
		AccountProfile document = findById(id.getValue());
		return modelMapper.map(document, AccountProfileDTO.class);
	}	
	
	/**
	 * 
	 * 
	 * @param accountProfile
	 * 
	 * 
	 */
	
	public void createAccountProfile(AccountProfileDTO accountProfile) {
		AccountProfile document = modelMapper.map(accountProfile, AccountProfile.class);
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
	
	public void updateAccountProfile(AccountProfileDTO accountProfile) {
		AccountProfile document = modelMapper.map(accountProfile, AccountProfile.class);
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
	
	public void deleteApplication(AccountProfileDTO accountProfile) {
		AccountProfile document = modelMapper.map(accountProfile, AccountProfile.class);
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
	
	public AccountProfileDTO findAccountProfileBySubject(String subject) {
		AccountProfile document = findOne( eq ( "href", subject) );
		AccountProfileDTO accountProfile = modelMapper.map(document, AccountProfileDTO.class);
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
	
	public AccountProfileDTO findAccountProfileByUsername(String username) {
		Optional<AccountProfile> document = Optional.ofNullable(findOne( eq( "username", username ) ) );
		AccountProfileDTO accountProfile = null;
		if (document.isPresent()) {
			accountProfile = modelMapper.map(document.get(), AccountProfileDTO.class);
		}
		return accountProfile; 
	}
}