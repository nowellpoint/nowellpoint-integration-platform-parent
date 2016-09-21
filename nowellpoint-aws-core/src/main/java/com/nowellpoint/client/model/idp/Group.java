package com.nowellpoint.client.model.idp;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Group implements Serializable {

	private static final long serialVersionUID = -4625854515221988485L;

	@JsonProperty(value="href")
	private String href;
	
	@JsonProperty(value="name")
	private String name;
	
	public Group() {
		
	}

	public String getHref() {
		return href;
	}

	public void setHref(String href) {
		this.href = href;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * "href": "https://api.stormpath.com/v1/groups/EngKafEhVNWT031mags1L",
        "name": "System Administrator",
        "description": "Sandbox System Administrator Group",
        "status": "ENABLED",
        "createdAt": "2016-04-14T12:04:24.937Z",
        "modifiedAt": "2016-04-14T12:05:55.674Z",
        "customData": {
          "href": "https://api.stormpath.com/v1/groups/EngKafEhVNWT031mags1L/customData"
        },
        "directory": {
          "href": "https://api.stormpath.com/v1/directories/4zwa2pcEmpENZbN2jZMtqD"
        },
        "tenant": {
          "href": "https://api.stormpath.com/v1/tenants/5h9eB7Q188wzjtkRa7SRq0"
        },
        "accounts": {
          "href": "https://api.stormpath.com/v1/groups/EngKafEhVNWT031mags1L/accounts"
        },
        "accountMemberships": {
          "href": "https://api.stormpath.com/v1/groups/EngKafEhVNWT031mags1L/accountMemberships"
        },
        "applications": {
          "href": "https://api.stormpath.com/v1/groups/EngKafEhVNWT031mags1L/applications"
        }
	 */
}