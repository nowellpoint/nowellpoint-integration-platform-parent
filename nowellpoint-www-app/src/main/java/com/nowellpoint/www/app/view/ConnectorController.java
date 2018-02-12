/**
 * 
 * Copyright 2015-2016 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */

package com.nowellpoint.www.app.view;

import java.util.Map;

import com.nowellpoint.client.NowellpointClient;
import com.nowellpoint.client.model.Connector;
import com.nowellpoint.client.model.ConnectorList;
import com.nowellpoint.client.model.ConnectorRequest;
import com.nowellpoint.client.model.CreateResult;
import com.nowellpoint.client.model.DeleteResult;
import com.nowellpoint.client.model.Token;
import com.nowellpoint.client.model.UpdateResult;
import com.nowellpoint.www.app.util.Path;
import com.nowellpoint.www.app.util.TemplateBuilder;

import freemarker.template.Configuration;
import spark.Request;
import spark.Response;

public class ConnectorController extends AbstractStaticController {
	
	public static class Template {
		public static final String CONNECTOR_VIEW = String.format(APPLICATION_CONTEXT, "connector-view.html");
		public static final String CONNECTOR_ADD = String.format(APPLICATION_CONTEXT, "connector-add.html");
		public static final String CONNECTOR_LIST = String.format(APPLICATION_CONTEXT, "connector-list.html");
		public static final String CONNECTOR_DETAIL = String.format(APPLICATION_CONTEXT, "connector-detail.html");
		public static final String CONNECTOR_EDIT = String.format(APPLICATION_CONTEXT, "connector-edit.html");
		public static final String SALESFORCE_CONNECTOR_SOBJECT_LIST = String.format(APPLICATION_CONTEXT, "salesforce-connector-sobject-list.html");
		public static final String SALESFORCE_CONNECTOR_SOBJECT_VIEW = String.format(APPLICATION_CONTEXT, "salesforce-connector-sobject-view.html");
		public static final String SALESFORCE_CONNECTOR_ADD_SERVICE = String.format(APPLICATION_CONTEXT, "salesforce-connector-add-service.html");
		public static final String SALESFORCE_CONNECTOR_FLOW_NEW = String.format(APPLICATION_CONTEXT, "salesforce-connector-flow-new.html");
	}
	
	/**
	 * 
	 * @param configuration
	 * @param request
	 * @param response
	 * @return
	 */
	
	public static String showConnectors(Configuration configuration, Request request, Response response) {		
		return render(ConnectorController.class, configuration, request, response, getModel(), Template.CONNECTOR_ADD);
	};
	
	/**
	 * 
	 * @param configuration
	 * @param request
	 * @param response
	 * @return
	 */
	
	public static String viewConnector(Configuration configuration, Request request, Response response) {
		Token token = getToken(request);

		String id = request.params(":id");

		Connector connector = NowellpointClient.defaultClient(token)
				.connector()
				.get(id);

		Map<String, Object> model = getModel();
		model.put("connector", connector);
		model.put("content", Template.CONNECTOR_VIEW);

		return render(ConnectorController.class, configuration, request, response, model, CONSOLE);
	};
	
	/**
	 * 
	 * @param configuration
	 * @param request
	 * @param response
	 * @return
	 */
	
	public static String listConnectors(Configuration configuration, Request request, Response response) {
		Token token = getToken(request);

		ConnectorList connectorList = NowellpointClient.defaultClient(token)
				.connector()
				.getConnectors();

		Map<String, Object> model = getModel();
		model.put("connectorsList", connectorList.getItems());

		return TemplateBuilder.template()
				.configuration(configuration)
				.controllerClass(ConnectorController.class)
				.identity(getIdentity(request))
				.locale(getLocale(request))
				.model(model).templateName(Template.CONNECTOR_LIST)
				.timeZone(getTimeZone(request))
				.build();
	}
	
	/**
	 * 
	 * @param configuration
	 * @param request
	 * @param response
	 * @return
	 */
	
	public static String addConnector(Configuration configuration, Request request, Response response) {
		Token token = getToken(request);

		String type = request.queryParams("type");

		ConnectorRequest connectorRequest = ConnectorRequest.builder()
				.token(token)
				.type(type)
				.build();

		CreateResult<Connector> createResult = NowellpointClient.defaultClient(token)
				.connector()
				.create(connectorRequest);
		
		if (createResult.isSuccess()) {
			response.header("Location", Path.Route.CONNECTORS_EDIT.replace(":id", createResult.getTarget().getId()));
			return "";
		} else {
			return showErrorMessage(ConnectorController.class, configuration, request, response, createResult.getErrorMessage());
		}
	};
	
	/**
	 * 
	 * @param configuration
	 * @param request
	 * @param response
	 * @return
	 */
	
	public static String editConnector(Configuration configuration, Request request, Response response) {
		Token token = getToken(request);

		String id = request.params(":id");

		Connector connector = NowellpointClient.defaultClient(token)
				.connector()
				.get(id);

		Map<String, Object> model = getModel();
		model.put("connector", connector);

		return render(ConnectorController.class, configuration, request, response, model, Template.CONNECTOR_EDIT);
	};

	/**
	 * 
	 * @param configuration
	 * @param request
	 * @param response
	 * @return
	 */

	public static String updateConnector(Configuration configuration, Request request, Response response) {
		Token token = getToken(request);

		String id = request.params(":id");
		String clientId = request.queryParamOrDefault("clientId", null);
		String clientSecret = request.queryParamOrDefault("clientSecret", null);
		String name = request.queryParamOrDefault("name", null);
		String username = request.queryParamOrDefault("username", null);
		String password = request.queryParamOrDefault("password", null);
		String type = request.queryParams("type");

		ConnectorRequest connectorRequest = ConnectorRequest.builder()
				.clientId(clientId)
				.clientSecret(clientSecret)
				.name(name)
				.username(username)
				.password(password)
				.token(token)
				.type(type)
				.build();

		UpdateResult<Connector> updateResult = NowellpointClient.defaultClient(token)
				.connector()
				.update(id, connectorRequest);
		
		if (updateResult.isSuccess()) {
			Map<String, Object> model = getModel();
			model.put("connector", updateResult.getTarget());			
			return render(ConnectorController.class, configuration, request, response, model, Template.CONNECTOR_DETAIL);
		} else {
			return showErrorMessage(ConnectorController.class, configuration, request, response, updateResult.getErrorMessage());
		}
	};
	
	public static String connectConnector(Configuration configuration, Request request, Response response) {
		Token token = getToken(request);
		
		String id = request.params(":id");
		String clientId = request.queryParamOrDefault("clientId", null);
		String clientSecret = request.queryParamOrDefault("clientSecret", null);
		String name = request.queryParamOrDefault("name", null);
		String username = request.queryParamOrDefault("username", null);
		String password = request.queryParamOrDefault("password", null);

		ConnectorRequest connectorRequest = ConnectorRequest.builder()
				.clientId(clientId)
				.clientSecret(clientSecret)
				.name(name)
				.username(username)
				.password(password)
				.token(token)
				.build();

		UpdateResult<Connector> updateResult = NowellpointClient.defaultClient(token)
				.connector()
				.connect(id, connectorRequest);
		
		if (updateResult.isSuccess()) {
			Map<String, Object> model = getModel();
			model.put("connector", updateResult.getTarget());			
			return render(ConnectorController.class, configuration, request, response, model, Template.CONNECTOR_DETAIL);
		} else {
			return showErrorMessage(ConnectorController.class, configuration, request, response, updateResult.getErrorMessage());
		}
	}
	
	/**
	 * 
	 * @param configuration
	 * @param request
	 * @param response
	 * @return
	 */

	public static String deleteConnector(Configuration configuration, Request request, Response response) {
		Token token = getToken(request);
		
		String id = request.params(":id");
		
		DeleteResult deleteResult = NowellpointClient.defaultClient(token)
				.connector()
				.delete(id);
		
		if (deleteResult.isSuccess()) {
			response.status(200);
			return "";
		} else {
			return showErrorMessage(ConnectorController.class, configuration, request, response, deleteResult.getErrorMessage());
		}
	};
	
	/**
	 * 
	 * @param configuration
	 * @param request
	 * @param response
	 * @return
	 */
	
	public static String disconnectConnector(Configuration configuration, Request request, Response response) {
		Token token = getToken(request);
		
		String id = request.params(":id");
		
		UpdateResult<Connector> updateResult = NowellpointClient.defaultClient(token)
				.connector()
				.disconnect(id);
		
		if (updateResult.isSuccess()) {
			Map<String, Object> model = getModel();
			model.put("connector", updateResult.getTarget());			
			return render(ConnectorController.class, configuration, request, response, model, Template.CONNECTOR_DETAIL);
		} else {
			return showErrorMessage(ConnectorController.class, configuration, request, response, updateResult.getErrorMessage());
		}
	};
	
	/**
	 * 
	 * @param configuration
	 * @param request
	 * @param response
	 * @return
	 */
	
	public static String refreshConnector(Configuration configuration, Request request, Response response) {
		Token token = getToken(request);
		
		String id = request.params(":id");
		
		UpdateResult<Connector> updateResult = NowellpointClient.defaultClient(token)
				.connector()
				.refresh(id);
		
		if (updateResult.isSuccess()) {
			Map<String, Object> model = getModel();
			model.put("connector", updateResult.getTarget());			
			return render(ConnectorController.class, configuration, request, response, model, Template.CONNECTOR_DETAIL);
		} else {
			return showErrorMessage(ConnectorController.class, configuration, request, response, updateResult.getErrorMessage());
		}
		
	}
}