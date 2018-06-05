package com.nowellpoint.console.entity;

import java.io.Serializable;

public class Photos implements Serializable {
	
	private static final long serialVersionUID = 2979641826151898765L;
	
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