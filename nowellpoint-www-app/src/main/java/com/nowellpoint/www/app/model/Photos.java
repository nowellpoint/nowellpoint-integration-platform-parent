package com.nowellpoint.www.app.model;

import java.io.Serializable;

public class Photos implements Serializable {
	
	/**
	 * 
	 */
	
	private static final long serialVersionUID = -8432369417826197600L;
	
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