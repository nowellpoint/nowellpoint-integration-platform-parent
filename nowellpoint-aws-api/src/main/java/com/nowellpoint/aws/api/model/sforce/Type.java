package com.nowellpoint.aws.api.model.sforce;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement( name = "Types" )
public class Type {
	
	private String members;
	
	private String name;
	
	public Type() {
		
	}

	public String getMembers() {
		return members;
	}

	public void setMembers(String members) {
		this.members = members;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}