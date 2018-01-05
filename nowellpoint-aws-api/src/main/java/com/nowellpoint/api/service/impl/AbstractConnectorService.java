package com.nowellpoint.api.service.impl;

import static com.mongodb.client.model.Filters.eq;
import static com.nowellpoint.util.Assert.assertNotNull;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.bson.types.ObjectId;

import com.nowellpoint.api.rest.domain.Connector;
import com.nowellpoint.api.rest.domain.ConnectorList;
import com.nowellpoint.api.rest.domain.ConnectorRequest;
import com.nowellpoint.api.rest.domain.ConnectorType;
import com.nowellpoint.api.rest.domain.SalesforceConnectionManager;
import com.nowellpoint.api.rest.domain.SalesforceConnectorWrapper;
import com.nowellpoint.api.rest.domain.UserInfo;
import com.nowellpoint.api.util.ClaimsContext;
import com.nowellpoint.api.util.MessageConstants;
import com.nowellpoint.api.util.MessageProvider;
import com.nowellpoint.aws.data.AbstractCacheService;
import com.nowellpoint.mongodb.DocumentManager;
import com.nowellpoint.mongodb.DocumentManagerFactory;
import com.nowellpoint.mongodb.document.MongoDocument;
import com.nowellpoint.util.Assert;

public class AbstractConnectorService extends AbstractCacheService {
	
	@Inject
	protected DocumentManagerFactory documentManagerFactory;
	
	private static final Map<String,ConnectorType> connectorTypes = createTypeMap();

	private static Map<String,ConnectorType> createTypeMap() {
		
		List<ConnectorType> connectorTypeList = new ArrayList<ConnectorType>();
		
		connectorTypeList.add(ConnectorType.builder()
				.name("SALESFORCE_SANDBOX")
				.scheme("salesforce")
				.grantType("password")
				.displayName("Salesforce Sandbox")
				.authEndpoint("https://test.salesforce.com")
				.iconHref("https://d3iep6okqojnln.cloudfront.net/salesforce-logo.png")
				.isSandbox(Boolean.TRUE)
				.build());
		
		connectorTypeList.add(ConnectorType.builder()
				.name("SALESFORCE_PRODUCTION")
				.scheme("salesforce")
				.grantType("password")
				.displayName("Salesforce Production")
				.authEndpoint("https://login.salesforce.com")
				.iconHref("https://d3iep6okqojnln.cloudfront.net/salesforce-logo.png")
				.isSandbox(Boolean.FALSE)
				.build());
		
		return Collections.unmodifiableMap(connectorTypeList.stream().collect(Collectors.toMap(t -> t.getName(), t -> t)));
	}
	
	protected Connector retrieve(String id) {		
		com.nowellpoint.api.model.document.Connector document = get(com.nowellpoint.api.model.document.Connector.class, id );
		if (Assert.isNull(document)) {
			DocumentManager documentManager = documentManagerFactory.createDocumentManager();
			document = documentManager.fetch(com.nowellpoint.api.model.document.Connector.class, new ObjectId( id ) );
			set(id, document);
		}
		Connector resource = Connector.of( document );
		return resource;
	}
	
	protected ConnectorList findAllByOwner(String ownerId) {
		DocumentManager documentManager = documentManagerFactory.createDocumentManager(); 
		Set<com.nowellpoint.api.model.document.Connector> documents = documentManager.find(
				com.nowellpoint.api.model.document.Connector.class,
				eq ( "owner", new ObjectId( ownerId ) ) );
		ConnectorList resources = new ConnectorList( documents );
		return resources;
	}
	
	protected void create(Connector connector) {
		MongoDocument document = connector.toDocument();
		DocumentManager documentManager = documentManagerFactory.createDocumentManager();
		documentManager.insertOne( document );
		connector.fromDocument(document);
		set(connector.getId(), document);
	}
	
	protected void update(Connector connector) {
		MongoDocument document = connector.toDocument();
		DocumentManager documentManager = documentManagerFactory.createDocumentManager();
		documentManager.replaceOne( document );
		connector.fromDocument(document);
		set(connector.getId(), document);
	}
	
	protected void delete(Connector connector) {
		MongoDocument document = connector.toDocument();
		DocumentManager documentManager = documentManagerFactory.createDocumentManager();
		documentManager.deleteOne(document);
		del(connector.getId());
	}
	
	protected Connector refreshConnector(Connector original) {
		if ("SALESFORCE_SANDBOX".equals(original.getConnectorType().getName()) || "SALESFORCE_PRODUCTION".equals(original.getConnectorType().getName())) {
			SalesforceConnectorWrapper wrapper = SalesforceConnectorWrapper.of(original);
			return wrapper.toConnector();
		}
		
		return null;		
	}
	
	protected Connector buildConnector(Connector original, ConnectorRequest request) {
		
		if (original.getIsConnected()) {
			
			Connector connector = Connector.builder()
					.from(original)
					.name(request.getName())
					.lastUpdatedBy(UserInfo.of(ClaimsContext.getClaims()))
					.lastUpdatedOn(Date.from(Instant.now()))
					.build();
			
			return connector;
		}
		
		if ("SALESFORCE_SANDBOX".equals(original.getConnectorType().getName()) || "SALESFORCE_PRODUCTION".equals(original.getConnectorType().getName())) {
			
			assertNotNull(request.getClientId(), MessageProvider.getMessage(Locale.getDefault(), MessageConstants.CONNECTOR_MISSING_CLIENT_ID));
			assertNotNull(request.getClientSecret(), MessageProvider.getMessage(Locale.getDefault(), MessageConstants.CONNECTOR_MISSING_CLIENT_SECRET));
			assertNotNull(request.getUsername(), MessageProvider.getMessage(Locale.getDefault(), MessageConstants.CONNECTOR_MISSING_USERNAME));
			assertNotNull(request.getPassword(), MessageProvider.getMessage(Locale.getDefault(), MessageConstants.CONNECTOR_MISSING_PASSWORD));
				
			SalesforceConnectionManager salesforceConnector = SalesforceConnectionManager.builder()
					.clientId(request.getClientId())
					.clientSecret(request.getClientSecret())
					.connector(original)
					.name(request.getName())
					.password(request.getPassword())
					.username(request.getUsername())
					.build();
				
			return salesforceConnector.toConnector();
		}
		
		return null;
	}
	
	protected Connector buildConnector(ConnectorRequest request) {
		
		ConnectorType connectorType = getConnectorType(request.getType());
		
		assertNotNull(connectorType, String.format(MessageProvider.getMessage(Locale.getDefault(), MessageConstants.CONNECTOR_INVALID_TYPE), request.getType()));
		
		if ("SALESFORCE_SANDBOX".equals(connectorType.getName()) || "SALESFORCE_PRODUCTION".equals(connectorType.getName())) {
			
			assertNotNull(request.getClientId(), MessageProvider.getMessage(Locale.getDefault(), MessageConstants.CONNECTOR_MISSING_CLIENT_ID));
			assertNotNull(request.getClientSecret(), MessageProvider.getMessage(Locale.getDefault(), MessageConstants.CONNECTOR_MISSING_CLIENT_SECRET));
			assertNotNull(request.getUsername(), MessageProvider.getMessage(Locale.getDefault(), MessageConstants.CONNECTOR_MISSING_USERNAME));
			assertNotNull(request.getPassword(), MessageProvider.getMessage(Locale.getDefault(), MessageConstants.CONNECTOR_MISSING_PASSWORD));
			
			SalesforceConnectionManager salesforceConnector = SalesforceConnectionManager.builder()
					.connectorType(connectorType)
					.clientId(request.getClientId())
					.clientSecret(request.getClientSecret())
					.name(request.getName())
					.password(request.getPassword())
					.username(request.getUsername())
					.build();
			
			return salesforceConnector.toConnector();
		}	
		
		return null;
	}
	
	private ConnectorType getConnectorType(String type) {
		return connectorTypes.get(type);
	}
}