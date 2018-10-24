package com.nowellpoint.console.model;

import org.immutables.value.Value;

@Value.Immutable
@Value.Modifiable
@Value.Style(typeImmutable = "*", jdkOnly=true)
public abstract class AbstractConnectionRequest {
    public abstract String getName();
    public abstract String getDomain();
    public abstract String getConnectedUser();
    public abstract String getInstanceUrl();
    public abstract String getEncryptedToken();
}