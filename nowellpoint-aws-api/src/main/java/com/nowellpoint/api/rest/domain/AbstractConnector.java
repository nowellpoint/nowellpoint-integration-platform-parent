package com.nowellpoint.api.rest.domain;

import java.util.Date;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.immutables.value.Value;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.nowellpoint.api.rest.ConnectorResource;
import com.nowellpoint.mongodb.document.MongoDocument;

@Value.Immutable
@Value.Modifiable
@Value.Style(typeImmutable = "*", jdkOnly=true, create = "new")
@JsonSerialize(as = Connector.class)
@JsonDeserialize(as = Connector.class)
public abstract class AbstractConnector extends AbstractImmutableResource {
	public abstract String getName();
	public abstract String getType();
	public abstract String getTypeName();
	public abstract String getAuthEndpoint();
	public abstract String getGrantType();
	public abstract String getIconHref();
	public abstract @JsonIgnore String getCredentialsKey();
	public abstract String getConnectionStatus();
	public abstract Date getConnectionDate();
	public abstract UserInfo getCreatedBy();
	public abstract UserInfo getLastUpdatedBy();
	public abstract OrganizationInfo getOwner();

	@Override
	public Meta getMeta() {
		return getMetaAs(ConnectorResource.class);
	}
	
	@Override
	public void fromDocument(MongoDocument document) {
		modelMapper.map(document, this);
	}
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(Connector.class);
	}
	
	@Override
	public MongoDocument toDocument() {
		return modelMapper.map(this, com.nowellpoint.api.model.document.Connector.class);
	}
	
	public static Connector of(com.nowellpoint.api.model.document.Connector source) {
		ModifiableConnector connector = modelMapper.map(source, ModifiableConnector.class);
		return connector.toImmutable();
	}
}