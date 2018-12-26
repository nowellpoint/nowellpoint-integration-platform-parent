package com.nowellpoint.console.view;

import static spark.Spark.get;
import static spark.Spark.post;

import java.io.IOException;
import java.nio.charset.Charset;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.TextStyle;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;
import javax.validation.ValidationException;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

import com.nowellpoint.console.entity.AggregationResult;
import com.nowellpoint.console.model.AddressRequest;
import com.nowellpoint.console.model.ContactRequest;
import com.nowellpoint.console.model.CreditCardRequest;
import com.nowellpoint.console.model.Organization;
import com.nowellpoint.console.model.Plan;
import com.nowellpoint.console.model.ProcessTemplateRequest;
import com.nowellpoint.console.model.StreamingEventListener;
import com.nowellpoint.console.model.StreamingEventListenerRequest;
import com.nowellpoint.console.model.Template;
import com.nowellpoint.console.service.ServiceClient;
import com.nowellpoint.console.util.Alert;
import com.nowellpoint.console.util.MessageProvider;
import com.nowellpoint.console.util.Path;
import com.nowellpoint.console.util.Templates;

import freemarker.template.Configuration;
import spark.Request;
import spark.Response;

public class OrganizationController extends BaseController {
	
	public static void configureRoutes(Configuration configuration) {
		get(Path.Route.ORGANIZATION, (request, response) 
				-> viewOrganization(request, response));
		
		get(Path.Route.ORGANIZATION_STREAMING_EVENTS, (request, response) 
				-> viewStreamingEvents(request, response));
		
		get(Path.Route.ORGANIZATION_STREAMING_EVENTS_SETUP, (request, response)
				-> setupStreamingEvents(request, response));
		
		post(Path.Route.ORGANIZATION_STREAMING_EVENTS_SETUP, (request, response)
				-> saveEventListener(request, response));
		
		get(Path.Route.ORGANIZATION_LIST_PLANS, (request, response) 
				-> listPlans(configuration, request, response));
		
		get(Path.Route.ORGANIZATION_PLAN, (request, response) 
				-> reviewPlan(configuration, request, response));
		
		post(Path.Route.ORGANIZATION_PLAN, (request, response) 
				-> setPlan(configuration, request, response));
		
		post(Path.Route.ORGANIZATION_CREDIT_CARD, (request, response) 
				-> updatePaymentMethod(configuration, request, response));
		
		post(Path.Route.ORGANIZATION_BILLING_ADDRESS, (request, response)
				-> updateBillingAddress(configuration, request, response));
		
//		delete(Path.Route.ORGANIZATION_CREDIT_CARD, (request, response) 
//				-> OrganizationController.removeCreditCard(configuration, request, response));
//		

//		
		post(Path.Route.ORGANIZATION_BILLING_CONTACT, (request, response) 
				-> updateBillingContact(configuration, request, response));
		
		get(Path.Route.ORGANIZATION_GET_INVOICE, (request, response) 
				-> getInvoice(configuration, request, response));
	}
	
	private static String viewOrganization(Request request, Response response) {

		String organizationId = getIdentity(request).getOrganization().getId();
		
		Organization organization = ServiceClient.getInstance()
				.organization()
				.get(organizationId);
		
		Map<String,Object> model = getModel();
		model.put("organization", organization);
    	
    	ProcessTemplateRequest templateProcessRequest = ProcessTemplateRequest.builder()
				.controllerClass(OrganizationController.class)
				.model(model)
				.templateName(Templates.ORGANIZATION)
				.build();
		
		return processTemplate(templateProcessRequest);
	}
	
	/**
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	
	private static String viewStreamingEvents(Request request, Response response) {
		
		String organizationId = getIdentity(request).getOrganization().getId();
		
		Organization organization = ServiceClient.getInstance()
				.organization()
				.get(organizationId);
		
		List<AggregationResult> results = ServiceClient.getInstance()
				.organization()
				.getEventsLastDays(organizationId, 7);
		
		String data = results.stream()
				.sorted(Comparator.reverseOrder())
				.map(r -> formatLabel(getIdentity(request).getLocale(), r))
				.collect(Collectors.joining(", "));
		
		Map<String,Object> model = getModel();
		model.put("organization", organization);
		model.put("results", results);
		model.put("data", data);
		
    	ProcessTemplateRequest templateProcessRequest = ProcessTemplateRequest.builder()
				.controllerClass(OrganizationController.class)
				.model(model)
				.templateName(Templates.ORGANIZATION_STREAMING_EVENTS)
				.build();
		
		return processTemplate(templateProcessRequest);
	};	
	
	/**
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	
	private static String setupStreamingEvents(Request request, Response response) {
		
		String source = request.params(":source");
		
		Organization organization = ServiceClient.getInstance()
				.organization()
				.get(getIdentity(request).getOrganization().getId());
		
		Optional<StreamingEventListener> eventListener = organization.getStreamingEventListeners()
				.stream()
				.filter(e -> source.equals(e.getSource()))
				.findFirst();
		
		Map<String,Object> model = getModel();
		model.put("organization", organization);
		model.put("eventListener", eventListener.get());
		
    	ProcessTemplateRequest templateProcessRequest = ProcessTemplateRequest.builder()
				.controllerClass(OrganizationController.class)
				.model(model)
				.templateName(Templates.ORGANIZATION_STREAMING_EVENTS_SETUP)
				.build();
		
		return processTemplate(templateProcessRequest);
	};	
	
	/**
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	
	private static String saveEventListener(Request request, Response response) {
		
		String sobject = request.params(":sobject");
		
		List<NameValuePair> params = Collections.emptyList();
		
		if (! request.body().isEmpty()) {
			params = URLEncodedUtils.parse(request.body(), Charset.forName("UTF-8"));
		}
		
		AtomicBoolean onCreate = new AtomicBoolean(Boolean.FALSE);
		AtomicBoolean onUpdate = new AtomicBoolean(Boolean.FALSE);
		AtomicBoolean onDelete = new AtomicBoolean(Boolean.FALSE);
		AtomicBoolean onUndelete = new AtomicBoolean(Boolean.FALSE);
		
		params.forEach(p -> {
			if ("create".equalsIgnoreCase(p.getValue())) {
				onCreate.set(Boolean.TRUE);
			}
			if ("update".equalsIgnoreCase(p.getValue())) {
				onUpdate.set(Boolean.TRUE);
			}
			if ("delete".equalsIgnoreCase(p.getValue())) {
				onDelete.set(Boolean.TRUE);
			}
			if ("undelete".equalsIgnoreCase(p.getValue())) {
				onUndelete.set(Boolean.TRUE);
			}
		});
		
		StreamingEventListenerRequest eventListenerRequest = StreamingEventListenerRequest.builder()
				.object(sobject)
				.onCreate(onCreate.get())
				.onUpdate(onUpdate.get())
				.onDelete(onDelete.get())
				.onUndelete(onUndelete.get())
				.build();
		
		Organization organization = ServiceClient.getInstance()
				.organization()
				.update(getIdentity(request).getOrganization().getId(), eventListenerRequest);
		
		Map<String,Object> model = getModel();
		model.put("organization", organization);
		model.put("SOBJECT", sobject);
		
    	ProcessTemplateRequest templateProcessRequest = ProcessTemplateRequest.builder()
				.controllerClass(OrganizationController.class)
				.model(model)
				.templateName(Templates.ORGANIZATION_STREAMING_EVENTS_SETUP)
				.build();
		
		return processTemplate(templateProcessRequest);
	};	
	
	/**
	 * 
	 * @param configuration
	 * @param request
	 * @param response
	 * @return
	 */
	
	private static String listPlans(Configuration configuration, Request request, Response response) {
		
		String id = request.params(":id");
		
		Organization organization = ServiceClient.getInstance()
				.organization()
				.get(id);
		
		List<Plan> plans = ServiceClient.getInstance()
				.plan()
				.getPlans(Locale.getDefault().toString());
		
		Template template = Template.builder()
				.configuration(configuration)
				.controllerClass(OrganizationController.class)
				.putModel("organization", organization)
				.putModel("plans", plans)
				.putModel("action", "listPlans")
				.request(request)
				.templateName(Templates.ORGANIZATION_CHANGE_PLAN)
				.build();
		
		return template.render();
	}
	
	/**
	 * 
	 * @param configuration
	 * @param request
	 * @param response
	 * @return
	 */
	
	private static String getInvoice(Configuration configuration, Request request, Response response) {
		
		String id = request.params(":id");
		String invoiceNumber = request.params(":invoiceNumber");
		
		try {
			
			byte[] data = ServiceClient.getInstance()
					.organization()
					.createInvoice(id, invoiceNumber);
				
			HttpServletResponse httpServletResponse = response.raw();
		    httpServletResponse.setContentType("application/pdf");
		    httpServletResponse.addHeader("Content-Disposition", String.format("inline; filename=invoice_%s.pdf", invoiceNumber));
		    httpServletResponse.getOutputStream().write(data);
		    httpServletResponse.getOutputStream().close();
				
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return "";
	}
	
	/**
	 * 
	 * @param configuration
	 * @param request
	 * @param response
	 * @return
	 */
	
	private static String setPlan(Configuration configuration, Request request, Response response) {
		
		String id = request.params(":id");
		String planId = request.params(":planId");
		String cardholderName = request.queryParamOrDefault("cardholderName", null);
		String number = request.queryParamOrDefault("number", null);
		String expirationMonth = request.queryParamOrDefault("expirationMonth", null);
		String expirationYear = request.queryParamOrDefault("expirationYear", null);
		String cvv = request.queryParamOrDefault("cvv", null);
		
		Plan plan = ServiceClient.getInstance()
				.plan()
				.get(planId);
		
		if ("FREE".equals(plan.getPlanCode()) || cardholderName == null) {
			ServiceClient.getInstance().organization().setPlan(id, plan);
		} else {
			
			CreditCardRequest creditCardRequest = CreditCardRequest.builder()
					.cardholderName(cardholderName)
					.cvv(cvv)
					.expirationMonth(expirationMonth)
					.expirationYear(expirationYear)
					.number(number)
					.build();
			
			try {
				ServiceClient.getInstance().organization().setPlan(id, plan, creditCardRequest);
			} catch (ValidationException e) {
				response.status(400);
				return Alert.showError(e.getMessage());
			}
		}

		return "";
	}
	
	/**
	 * 
	 * @param configuration
	 * @param request
	 * @param response
	 * @return
	 */
	
	private static String reviewPlan(Configuration configuration, Request request, Response response) {
		
		String id = request.params(":id");
		String planId = request.params(":planId");
		
		Organization organization = ServiceClient.getInstance()
				.organization()
				.get(id);
		
		Plan plan = ServiceClient.getInstance()
				.plan()
				.get(planId);
		
		Template template = Template.builder()
				.configuration(configuration)
				.controllerClass(OrganizationController.class)
				.putModel("organization", organization)
				.putModel("plan", plan)
				.putModel("action", "reviewPlan")
				.request(request)
				.templateName(Templates.ORGANIZATION_CHANGE_PLAN)
				.build();
		
		return template.render();
	}
	
	/**
	 * 
	 * @param configuration
	 * @param request
	 * @param response
	 * @return
	 */
	
	private static String updateBillingAddress(Configuration configuration, Request request, Response response) {
		
		String id = request.params(":id");
		
		String street = request.queryParams("street");
		String postalCode = request.queryParams("postalCode");
		String city = request.queryParams("city");
		String countryCode = request.queryParams("countryCode");
		String state = request.queryParamOrDefault("state", null);
		
		AddressRequest addressRequest = AddressRequest.builder()
				.city(city)
				.countryCode(countryCode)
				.postalCode(postalCode)
				.state(state)
				.street(street)
				.build();
		
		try {
			
			Organization organization = ServiceClient.getInstance()
					.organization()
					.update(id, addressRequest);
			
			Template template = Template.builder()
					.configuration(configuration)
					.controllerClass(OrganizationController.class)
					.putModel("organization", organization)
					.request(request)
					.templateName(Templates.ORGANIZATION_BILLING_ADDRESS)
					.build();
			
			return template.render();
			
		} catch (ValidationException e) {
			
			response.status(400);
			
			return Alert.showError(e.getMessage());
		}
	}
	
	/**
	 * 
	 * @param configuration
	 * @param request
	 * @param response
	 * @return
	 */
	
	private static String updateBillingContact(Configuration configuration, Request request, Response response) {
		String id = request.params(":id");
		
		String firstName = request.queryParams("firstName");
		String lastName = request.queryParams("lastName");
		String email = request.queryParams("email");
		String phone = request.queryParamOrDefault("phone", null);
		
		ContactRequest contactRequest = ContactRequest.builder()
				.email(email)
				.firstName(firstName)
				.lastName(lastName)
				.phone(phone)
				.build();
		
		try {
			
			Organization organization = ServiceClient.getInstance()
					.organization()
					.update(id, contactRequest);
			
			Template template = Template.builder()
					.configuration(configuration)
					.controllerClass(OrganizationController.class)
					.putModel("organization", organization)
					.request(request)
					.templateName(Templates.ORGANIZATION_BILLING_CONTACT)
					.build();
			
			return template.render();
			
		} catch (ValidationException e) {
			
			response.status(400);
			
			return Alert.showError(e.getMessage());
		}
	}
	
	/**
	 * 
	 * @param configuration
	 * @param request
	 * @param response
	 * @return
	 */
	
	private static String updatePaymentMethod(Configuration configuration, Request request, Response response) {
		
		String id = request.params(":id");
		
		String cardholderName = request.queryParams("cardholderName");
		String expirationMonth = request.queryParams("expirationMonth");
		String expirationYear = request.queryParams("expirationYear");
		String number = request.queryParams("number");
		String cvv = request.queryParams("cvv");
		
		CreditCardRequest creditCardRequest = CreditCardRequest.builder()
				.cardholderName(cardholderName)
				.cvv(cvv)
				.expirationMonth(expirationMonth)
				.expirationYear(expirationYear)
				.number(number)
				.build();
		
		try {
			
			Organization organization = ServiceClient.getInstance()
					.organization()
					.update(id, creditCardRequest);
			
			Template template = Template.builder()
					.configuration(configuration)
					.controllerClass(OrganizationController.class)
					.putModel("organization", organization)
					.request(request)
					.templateName(Templates.ORGANIZATION_PAYMENT_METHOD)
					.build();
			
			return template.render();
			
		} catch (ValidationException e) {
			
			response.status(400);
			
			return Alert.showError(e.getMessage());
		}
	};
	
	/**
	 * 
	 * @param configuration
	 * @param request
	 * @param response
	 * @return
	 */
	
	public static String removeCreditCard(Configuration configuration, Request request, Response response) {
//		Token token = getToken(request);
//		
//		String organizationId = request.params(":id");
//		
//		CreditCardRequest creditCardRequest = CreditCardRequest.builder()
//				.organizationId(organizationId)
//				.token(token)
//				.build();
//		
//		UpdateResult<Organization> updateResult = NowellpointClient.defaultClient(token)
//				.organization()
//				.subscription()
//				.creditCard()
//				.remove(creditCardRequest);
//		
//		if (updateResult.isSuccess()) {
//			
//			Map<String, Object> model = getModel();
//			model.put("organization", updateResult.getTarget());
//			
//			return render(OrganizationController.class, configuration, request, response, model, Template.ORGANIZATION);
//			
//		} else {
//			return showErrorMessage(OrganizationController.class, configuration, request, response, updateResult.getErrorMessage());
//		}
		
		return null;
	};
	
	private static String formatLabel(Locale locale, AggregationResult result) {
		
		ZoneId utc = ZoneId.of( "UTC" );
		
		LocalDate now = LocalDate.now( utc ).minusDays(Integer.valueOf(result.getId()));
		
		String text = null;
		if (now.equals(LocalDate.now( utc ))) {
			text = MessageProvider.getMessage(locale, "today");
		} else if (now.equals(LocalDate.now( utc ).minusDays(1))) {
			text = MessageProvider.getMessage(locale, "yesterday");
		} else {
			text = now.getDayOfWeek().getDisplayName(TextStyle.FULL, locale);
		}
		
		return new StringBuilder("['").append(text)
				.append("'")
				.append(", ")
				.append(result.getCount())
				.append("]")
				.toString();
	}
}