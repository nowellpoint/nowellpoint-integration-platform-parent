package com.nowellpoint.api.model;

import java.io.Serializable;

public class Photos implements Serializable {
	
	/**
	 * 
	 */
	
	private static final long serialVersionUID = 5996939557532376149L;
	
	/**
	 * 
	 */
	
	private String profilePicture;
	
	public Photos() {
		
	}

	public String getProfilePicture() {
		return profilePicture;
	}

	public void setProfilePicture(String profilePicture) {
		this.profilePicture = profilePicture;
	}
}