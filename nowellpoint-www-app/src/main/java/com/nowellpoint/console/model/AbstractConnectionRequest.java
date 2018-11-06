package com.nowellpoint.console.model;

import org.immutables.value.Value;

import com.nowellpoint.client.sforce.model.Identity;
import com.nowellpoint.client.sforce.model.Organization;
import com.nowellpoint.client.sforce.model.Token;

@Value.Immutable
@Value.Modifiable
@Value.Style(typeImmutable = "*", jdkOnly=true)
public abstract class AbstractConnectionRequest {
    public abstract String getName();
    public abstract String getDomain();
    public abstract String getConnectedUser();
    public abstract String getInstanceUrl();
    public abstract String getEncryptedToken();
    
    public abstract Identity getIdentity();
    public abstract Token getToken();
    public abstract Organization getOrganization();
}