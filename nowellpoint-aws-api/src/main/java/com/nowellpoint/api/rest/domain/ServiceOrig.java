package com.nowellpoint.api.rest.domain;

import java.time.Instant;
import java.util.Date;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import com.fasterxml.jackson.annotation.JsonFormat;

public class ServiceOrig {
	
	private String serviceId;
	
	private String name;
	
	private String type;
	
	private String template;
	
	private String href;
	
	private String whatId;
	
	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
	private Date addedOn;

	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
	private Date updatedOn;
	
	public static ServiceOrig of(JobType jobType) {
		return new ServiceOrig(jobType);
	}
	
	private ServiceOrig(JobType jobType) {
		this.serviceId = jobType.getId();
		this.name = jobType.getName();
		this.type = jobType.getCode();
		this.template = jobType.getTemplate();
		this.href = jobType.getHref();
		this.addedOn = Date.from(Instant.now());
		this.updatedOn = Date.from(Instant.now());
	}
	
	private ServiceOrig() {

	}

	public String getServiceId() {
		return serviceId;
	}

	public void setId(String serviceId) {
		this.serviceId = serviceId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getTemplate() {
		return template;
	}

	public void setTemplate(String template) {
		this.template = template;
	}

	public String getHref() {
		return href;
	}

	public void setHref(String href) {
		this.href = href;
	}

	public String getWhatId() {
		return whatId;
	}

	public void setWhatId(String whatId) {
		this.whatId = whatId;
	}

	public void setServiceId(String serviceId) {
		this.serviceId = serviceId;
	}

	public Date getAddedOn() {
		return addedOn;
	}

	public void setAddedOn(Date addedOn) {
		this.addedOn = addedOn;
	}

	public Date getUpdatedOn() {
		return updatedOn;
	}

	public void setUpdatedOn(Date updatedOn) {
		this.updatedOn = updatedOn;
	}
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder()
				.append(this.serviceId)
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
		ServiceOrig serviceOrig = (ServiceOrig) obj;
		return new EqualsBuilder()
				.append(this.serviceId, serviceOrig.serviceId)
				.isEquals();
	}
}