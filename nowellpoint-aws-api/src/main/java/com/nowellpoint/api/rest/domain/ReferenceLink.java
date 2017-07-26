package com.nowellpoint.api.rest.domain;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class ReferenceLink {
	
	private String id;
	
	private String name;
	
	public ReferenceLink() {
		
	}
	
	private ReferenceLink(String name, String id) {
		this.name = name;
		this.id = id;
	}
	
	public static ReferenceLink of (ReferenceLinkTypes type, String id) {
		return new ReferenceLink(type.name(), id);
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder()
				.append(this.id)
		        .toHashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) { 
			return false;
		}
		if (obj == this) { 
			return true;
		}
		if (obj.getClass() != getClass()) {
			return false;
		}
		ReferenceLink referenceLink = (ReferenceLink) obj;
		return new EqualsBuilder()
				.appendSuper(super.equals(obj))
				.append(this.name, referenceLink.name)
				.isEquals();
	}
}