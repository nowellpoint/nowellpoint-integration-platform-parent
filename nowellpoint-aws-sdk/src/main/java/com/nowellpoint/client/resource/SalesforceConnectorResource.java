package com.nowellpoint.client.resource;

import com.nowellpoint.aws.http.HttpResponse;
import com.nowellpoint.aws.http.MediaType;
import com.nowellpoint.aws.http.RestResource;
import com.nowellpoint.aws.http.Status;
import com.nowellpoint.client.model.CreateResult;
import com.nowellpoint.client.model.CreateSalesforceConnectorRequest;
import com.nowellpoint.client.model.DeleteResult;
import com.nowellpoint.client.model.EnvironmentRequest;
import com.nowellpoint.client.model.Error;
import com.nowellpoint.client.model.SObjectDetail;
import com.nowellpoint.client.model.Instance;
import com.nowellpoint.client.model.SalesforceConnector;
import com.nowellpoint.client.model.SalesforceConnectorList;
import com.nowellpoint.client.model.SalesforceConnectorRequest;
import com.nowellpoint.client.model.Token;
import com.nowellpoint.client.model.UpdateResult;
import com.nowellpoint.client.model.exception.NotFoundException;
import com.nowellpoint.client.model.exception.ServiceUnavailableException;

public class SalesforceConnectorResource extends AbstractResource {
	
	private static final String RESOURCE_CONTEXT = "salesforce-connectors";
	
	public SalesforceConnectorResource(Token token) {
		super(token);
	}
	
	public CreateResult<SalesforceConnector> create(CreateSalesforceConnectorRequest request) {
		HttpResponse httpResponse = RestResource.post(token.getEnvironmentUrl())
				.bearerAuthorization(token.getAccessToken())
				.header("Content-Type", MediaType.APPLICATION_FORM_URLENCODED)
				.path(RESOURCE_CONTEXT)
    			.parameter("id", request.getId())
    			.parameter("instanceUrl", request.getInstanceUrl())
    			.parameter("accessToken", request.getAccessToken())
    			.parameter("refreshToken", request.getRefreshToken())
    			.execute();
		
		CreateResult<SalesforceConnector> result = null;
    	
    	if (httpResponse.getStatusCode() == Status.CREATED) {
    		SalesforceConnector resource = httpResponse.getEntity(SalesforceConnector.class);
    		result = new CreateResultImpl<SalesforceConnector>(resource);
    	} else {
    		Error error = httpResponse.getEntity(Error.class);
    		result = new CreateResultImpl<SalesforceConnector>(error);
    	}
    	
    	return result;
	}
	
	public SalesforceConnectorList getSalesforceConnectors() {
		HttpResponse httpResponse = RestResource.get(token.getEnvironmentUrl())
				.bearerAuthorization(token.getAccessToken())
				.path(RESOURCE_CONTEXT)
    			.execute();
		
		SalesforceConnectorList resources = null;
		
		if (httpResponse.getStatusCode() == Status.OK) {
			resources = httpResponse.getEntity(SalesforceConnectorList.class);
    	} else {
    		throw new ServiceUnavailableException(httpResponse.getAsString());
    	}
    	
    	return resources;
	}
	
	public SalesforceConnector get(String id) {
		HttpResponse httpResponse = RestResource.get(token.getEnvironmentUrl())
				.bearerAuthorization(token.getAccessToken())
				.header("Content-Type", MediaType.APPLICATION_FORM_URLENCODED)
				.path(RESOURCE_CONTEXT)
    			.path(id)
    			.execute();
		
		SalesforceConnector resource = null;
		
		if (httpResponse.getStatusCode() == Status.OK) {
			resource = httpResponse.getEntity(SalesforceConnector.class);
    	} else if (httpResponse.getStatusCode() == Status.NOT_FOUND) {
			throw new NotFoundException(httpResponse.getAsString());
		} else {
			throw new ServiceUnavailableException(httpResponse.getAsString());
    	}
    	
    	return resource;
	}
	
	public UpdateResult<SalesforceConnector> update(String id, SalesforceConnectorRequest salesforceConnectorRequest) {
		HttpResponse httpResponse = RestResource.post(token.getEnvironmentUrl())
				.bearerAuthorization(token.getAccessToken())
				.header("Content-Type", MediaType.APPLICATION_FORM_URLENCODED)
				.path(RESOURCE_CONTEXT)
    			.path(id)
    			.parameter("tag", salesforceConnectorRequest.getTag())
    			.parameter("name", salesforceConnectorRequest.getName())
    			.execute();
		
		UpdateResult<SalesforceConnector> result = null;
		
		if (httpResponse.getStatusCode() == Status.OK) {
			SalesforceConnector resource = httpResponse.getEntity(SalesforceConnector.class);
			result = new UpdateResultImpl<SalesforceConnector>(resource);  
		} else if (httpResponse.getStatusCode() == Status.NOT_FOUND) {
			throw new NotFoundException(httpResponse.getAsString());
		} else if (httpResponse.getStatusCode() == Status.BAD_REQUEST) {
			Error error = httpResponse.getEntity(Error.class);
			result = new UpdateResultImpl<SalesforceConnector>(error);
		}
		
		return result;
	}
	
	public DeleteResult delete(String id) {
		HttpResponse httpResponse = RestResource.delete(token.getEnvironmentUrl())
				.bearerAuthorization(token.getAccessToken())
				.path(RESOURCE_CONTEXT)
    			.path(id)
    			.execute();
		
		DeleteResult deleteResult = null;
		
		if (httpResponse.getStatusCode() == Status.OK) {
			deleteResult = new DeleteResultImpl();
		} else if (httpResponse.getStatusCode() == Status.BAD_REQUEST) {
			Error error = httpResponse.getEntity(Error.class);
			deleteResult = new DeleteResultImpl(error);
		}
		
		return deleteResult;
	}
	
	public InstanceResource instance() {
		return new InstanceResource(token);
	}
	
	public class SObjectDetailResource extends AbstractResource {
		
		public SObjectDetailResource(Token token) {
			super(token);
		}
		
		public SObjectDetail get(String salesforceConnectorId, String key, String sobjectName) {
			HttpResponse httpResponse = RestResource.get(token.getEnvironmentUrl())
					.bearerAuthorization(token.getAccessToken())
					.header("Content-Type", MediaType.APPLICATION_FORM_URLENCODED)
					.path(RESOURCE_CONTEXT)
	    			.path(salesforceConnectorId)
	    			.path("instance")
	    			.path(key)
	    			.path("sobject")
	    			.path(sobjectName)
	    			.execute();
			
			SObjectDetail resource = null;
			
			if (httpResponse.getStatusCode() == Status.OK) {
				resource = httpResponse.getEntity(SObjectDetail.class); 
			} else if (httpResponse.getStatusCode() == Status.NOT_FOUND) {
				throw new NotFoundException(httpResponse.getAsString());
			} 
			
			return resource;
		}
	}
	
	public class InstanceResource extends AbstractResource {
		
		public InstanceResource(Token token) {
			super(token);
		}
		
		public Instance get(String salesforceConnectorId, String key) {
			HttpResponse httpResponse = RestResource.get(token.getEnvironmentUrl())
					.bearerAuthorization(token.getAccessToken())
					.header("Content-Type", MediaType.APPLICATION_FORM_URLENCODED)
					.path(RESOURCE_CONTEXT)
	    			.path(salesforceConnectorId)
	    			.path("instance")
	    			.path(key)
	    			.execute();
			
			Instance resource = null;
			
			if (httpResponse.getStatusCode() == Status.OK) {
				resource = httpResponse.getEntity(Instance.class);  
			} else if (httpResponse.getStatusCode() == Status.NOT_FOUND) {
				throw new NotFoundException(httpResponse.getAsString());
			} 
			
			return resource;
		}
		
		public CreateResult<Instance> add(String salesforceConnectorId, EnvironmentRequest environmentRequest) {
			Instance instance = new Instance()
					.withIsActive(environmentRequest.getIsActive())
					.withAuthEndpoint(environmentRequest.getAuthEndpoint())
					.withEnvironmentName(environmentRequest.getEnvironmentName())
					.withPassword(environmentRequest.getPassword())
					.withUsername(environmentRequest.getUsername())
					.withSecurityToken(environmentRequest.getSecurityToken());
			
			HttpResponse httpResponse = RestResource.post(token.getEnvironmentUrl())
					.bearerAuthorization(token.getAccessToken())
					.contentType(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_JSON)
					.path(RESOURCE_CONTEXT)
	    			.path(salesforceConnectorId)
	    			.path("instance")
	    			.body(instance)
					.execute();
			
			CreateResult<Instance> result = null;
			
			if (httpResponse.getStatusCode() == Status.OK) {
				Instance resource = httpResponse.getEntity(Instance.class);
				result = new CreateResultImpl<Instance>(resource);  
			} else if (httpResponse.getStatusCode() == Status.BAD_REQUEST) {
				Error error = httpResponse.getEntity(Error.class);
				result = new CreateResultImpl<Instance>(error);
			}
			
			return result;
		}
		
		public UpdateResult<Instance> update(String salesforceConnectorId, String key, EnvironmentRequest environmentRequest) {
			Instance instance = new Instance()
					.withIsActive(environmentRequest.getIsActive())
					.withAuthEndpoint(environmentRequest.getAuthEndpoint())
					.withEnvironmentName(environmentRequest.getEnvironmentName())
					.withPassword(environmentRequest.getPassword())
					.withUsername(environmentRequest.getUsername())
					.withSecurityToken(environmentRequest.getUsername());
			
			HttpResponse httpResponse = RestResource.put(token.getEnvironmentUrl())
					.contentType(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_JSON)
					.bearerAuthorization(token.getEnvironmentUrl())
					.path(RESOURCE_CONTEXT)
	    			.path(salesforceConnectorId)
	    			.path("instance")
	    			.path(key)
	    			.body(instance)
					.execute();
			
			UpdateResult<Instance> result = null;
			
			if (httpResponse.getStatusCode() == Status.OK) {
				Instance resource = httpResponse.getEntity(Instance.class);
				result = new UpdateResultImpl<Instance>(resource);  
			} else if (httpResponse.getStatusCode() == Status.NOT_FOUND) {
				throw new NotFoundException(httpResponse.getAsString());
			} else if (httpResponse.getStatusCode() == Status.BAD_REQUEST) {
				Error error = httpResponse.getEntity(Error.class);
				result = new UpdateResultImpl<Instance>(error);
			}
			
			return result;
		}
		
		public DeleteResult delete(String salesforceConnectorId, String key) {
			HttpResponse httpResponse = RestResource.delete(token.getEnvironmentUrl())
					.bearerAuthorization(token.getAccessToken())
					.path(RESOURCE_CONTEXT)
	    			.path(salesforceConnectorId)
	    			.path("instance")
	    			.path(key)
					.execute();
			
			DeleteResult deleteResult = null;
			
			if (httpResponse.getStatusCode() == Status.OK) {
				deleteResult = new DeleteResultImpl();
			} else if (httpResponse.getStatusCode() == Status.BAD_REQUEST) {
				Error error = httpResponse.getEntity(Error.class);
				deleteResult = new DeleteResultImpl(error);
			}
			
			return deleteResult;
		}
		
		public UpdateResult<Instance> test(String salesforceConnectorId, String key) {
			HttpResponse httpResponse = RestResource.post(token.getEnvironmentUrl())
					.bearerAuthorization(token.getAccessToken())
					.header("Content-Type", MediaType.APPLICATION_FORM_URLENCODED)
					.path(RESOURCE_CONTEXT)
	    			.path(salesforceConnectorId)
	    			.path("instance")
	    			.path(key)
	    			.path("actions")
	    			.path("test")
	    			.path("invoke")
	    			.execute();
			
			UpdateResult<Instance> result = null;
			
			if (httpResponse.getStatusCode() == Status.OK) {
				Instance resource = httpResponse.getEntity(Instance.class);
				result = new UpdateResultImpl<Instance>(resource);  
			} else if (httpResponse.getStatusCode() == Status.NOT_FOUND) {
				throw new NotFoundException(httpResponse.getAsString());
			} else if (httpResponse.getStatusCode() == Status.BAD_REQUEST) {
				Error error = httpResponse.getEntity(Error.class);
				result = new UpdateResultImpl<Instance>(error);
			}
			
			return result;
		}
		
		public UpdateResult<Instance> build(String salesforceConnectorId, String key) {
			HttpResponse httpResponse = RestResource.post(token.getEnvironmentUrl())
					.bearerAuthorization(token.getAccessToken())
					.header("Content-Type", MediaType.APPLICATION_FORM_URLENCODED)
					.path(RESOURCE_CONTEXT)
	    			.path(salesforceConnectorId)
	    			.path("instance")
	    			.path(key)
	    			.path("actions")
	    			.path("build")
	    			.path("invoke")
	    			.execute();
			
			UpdateResult<Instance> result = null;
			
			if (httpResponse.getStatusCode() == Status.OK) {
				Instance resource = httpResponse.getEntity(Instance.class);
				result = new UpdateResultImpl<Instance>(resource);  
			} else if (httpResponse.getStatusCode() == Status.NOT_FOUND) {
				throw new NotFoundException(httpResponse.getAsString());
			} else if (httpResponse.getStatusCode() == Status.BAD_REQUEST) {
				Error error = httpResponse.getEntity(Error.class);
				result = new UpdateResultImpl<Instance>(error);
			}
			
			return result;
		}
		
		public SObjectDetailResource sobjectDetail() {
			return new SObjectDetailResource(token);
		}
	}
}