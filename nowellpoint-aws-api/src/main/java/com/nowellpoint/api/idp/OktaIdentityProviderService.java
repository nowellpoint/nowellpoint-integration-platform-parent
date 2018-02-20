package com.nowellpoint.api.idp;

import com.nowellpoint.api.service.IdentityProviderService;
import com.nowellpoint.api.util.EnvUtil;
import com.nowellpoint.api.util.EnvUtil.Variable;
import com.okta.sdk.authc.credentials.TokenClientCredentials;
import com.okta.sdk.client.Client;
import com.okta.sdk.client.Clients;
import com.okta.sdk.resource.user.ChangePasswordRequest;
import com.okta.sdk.resource.user.PasswordCredential;
import com.okta.sdk.resource.user.User;
import com.okta.sdk.resource.user.UserBuilder;
import com.okta.sdk.resource.user.UserCredentials;
import com.okta.sdk.resource.user.UserProfile;

public class OktaIdentityProviderService implements IdentityProviderService {
	
	private static Client client;
	
	static {
		
		client = Clients.builder()
				.setClientCredentials(new TokenClientCredentials(EnvUtil.getValue(Variable.OKTA_API_KEY)))
				.setOrgUrl(EnvUtil.getValue(Variable.OKTA_ORG_URL))
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
		
		User user = UserBuilder.instance()
				.setEmail(email)
				.setLogin(email)
				.setFirstName(firstName)
				.setLastName(lastName)
				.setActive(Boolean.TRUE)
				.addGroup(EnvUtil.getValue(Variable.OKTA_GROUP_ID))
				.buildAndCreate(client);
		
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
	 * @param id
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