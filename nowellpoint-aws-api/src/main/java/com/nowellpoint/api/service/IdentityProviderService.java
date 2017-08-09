package com.nowellpoint.api.service;

import com.okta.sdk.resource.user.User;

public interface IdentityProviderService {
	
	/**
	 * 
	 * @param href
	 * @return
	 */
	
	public User getUser(String id);
	
	/**
	 * 
	 * @param email
	 * @param firstName
	 * @param lastName
	 */
	
	public User createUser(String email, String firstName, String lastName, String password);
	
	/**
	 * 
	 * @param href
	 * @param email
	 * @param firstName
	 * @param lastName
	 */
	
	public User updateUser(String id, String email, String firstName, String lastName);
	
	/**
	 * 
	 * @param href
	 */
	
	public void deactivateUser(String id);
	
	/**
	 * 
	 * @param id
	 * @param password
	 */
	
	public void setPassword(String id, String password);
	
	/**
	 * 
	 * @param id
	 * @param oldPassword
	 * @param newPassword
	 */
	
	public void changePassword(String id, String oldPassword, String newPassword);
	
	/**
	 * 
	 * @param id
	 */
	
	public void deleteUser(String id);
}