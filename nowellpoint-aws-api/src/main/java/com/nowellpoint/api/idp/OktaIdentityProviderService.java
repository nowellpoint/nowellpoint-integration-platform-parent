package com.nowellpoint.api.idp;

import com.nowellpoint.api.service.IdentityProviderService;
import com.nowellpoint.util.Properties;
import com.okta.sdk.authc.credentials.TokenClientCredentials;
import com.okta.sdk.client.Client;
import com.okta.sdk.client.Clients;
import com.okta.sdk.resource.user.ChangePasswordRequest;
import com.okta.sdk.resource.user.PasswordCredential;
import com.okta.sdk.resource.user.User;
import com.okta.sdk.resource.user.UserCredentials;
import com.okta.sdk.resource.user.UserProfile;
import com.okta.sdk.resource.user.UserStatus;

public class OktaIdentityProviderService implements IdentityProviderService {
	
	private static Client client;
	
	static {
		
		client = Clients.builder()
				.setClientCredentials(new TokenClientCredentials(System.getProperty(Properties.OKTA_API_KEY)))
				.setOrgUrl(System.getProperty(Properties.OKTA_ORG_URL))
				.build();
	}	
	
	/**
	 * 
	 * @param href
	 * @return
	 */
	
	@Override
	public User getUser(String id) {
		return client.getUser(id);
	}
	
	/**
	 * 
	 * @param email
	 * @param firstName
	 * @param lastName
	 * @param password
	 */
	
	@Override
	public User createUser(String email, String firstName, String lastName, String password) {	
		
		System.out.println(System.getProperty(Properties.OKTA_API_KEY));
		
		UserProfile userProfile = client.instantiate(UserProfile.class)
			    .setEmail(email)
			    .setLogin(email)
			    .setFirstName(firstName)
			    .setLastName(lastName);
		
		PasswordCredential passwordCredential = client.instantiate(PasswordCredential.class)
				.setValue(password);
		
		UserCredentials userCredentials = client.instantiate(UserCredentials.class)
				.setPassword(passwordCredential);
		
		User user = client.instantiate(User.class)
				.setProfile(userProfile)
				.setStatus(UserStatus.ACTIVE)
				.setCredentials(userCredentials);
		
		user = client.createUser(user);
		
		client.getUser(user.getId()).addToGroup(System.getProperty(Properties.OKTA_GROUP_ID));
		
		return user;
	}
	
	/**
	 * 
	 */
	
	@Override
	public User updateUser(String id, String email, String firstName, String lastName) {
		
		User user = client.getUser(id);
		
		UserProfile userProfile = user.getProfile()
				.setLogin(email)
			    .setEmail(email)
			    .setFirstName(firstName)
			    .setLastName(lastName);
		
		user.setProfile(userProfile).update();
		
		return user;
	}
	
	/**
	 * 
	 * @param href
	 */
	
	@Override
	public void deactivateUser(String id) {
		client.getUser(id).deactivate();
	}
	
	/**
	 * 
	 */
	
	@Override
	public void setPassword(String id, String password) {
		PasswordCredential passwordCredential = client.instantiate(PasswordCredential.class)
				.setValue(password);
		
		UserCredentials userCredentials = client.instantiate(UserCredentials.class)
				.setPassword(passwordCredential);
		
		client.getUser(id).setCredentials(userCredentials).update();
	}
	
	/**
	 * 
	 */
	
	@Override
	public void changePassword(String id, String oldPassword, String newPassword) {
		ChangePasswordRequest changePasswordRequest = client.instantiate(ChangePasswordRequest.class)
				.setNewPassword(client.instantiate(PasswordCredential.class)
						.setValue(newPassword))
				.setOldPassword(client.instantiate(PasswordCredential.class)
						.setValue(oldPassword));
		
		client.getUser(id).changePassword(changePasswordRequest);
	}
	
	/**
	 * 
	 */
	
	@Override
	public void deleteUser(String id) {
		client.getUser(id).delete();
	}
}