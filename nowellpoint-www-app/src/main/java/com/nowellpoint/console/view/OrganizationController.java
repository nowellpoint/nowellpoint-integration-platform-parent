package com.nowellpoint.console.view;

import static spark.Spark.get;
import static spark.Spark.post;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import javax.servlet.http.HttpServletResponse;
import javax.validation.ValidationException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import com.nowellpoint.console.model.AddressRequest;
import com.nowellpoint.console.model.ContactRequest;
import com.nowellpoint.console.model.CreditCardRequest;
import com.nowellpoint.console.model.Organization;
import com.nowellpoint.console.model.Plan;
import com.nowellpoint.console.model.Template;
import com.nowellpoint.console.model.Transaction;
import com.nowellpoint.console.service.ServiceClient;
import com.nowellpoint.console.util.Alert;
import com.nowellpoint.console.util.Templates;
import com.nowellpoint.www.app.util.Path;

import freemarker.template.Configuration;
import spark.Request;
import spark.Response;

public class OrganizationController extends BaseController {
	
	public static void configureRoutes(Configuration configuration) {
		get(Path.Route.ORGANIZATION_VIEW, (request, response) 
				-> viewOrganization(configuration, request, response));
		
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
	
	private static String viewOrganization(Configuration configuration, Request request, Response response) {

		String id = request.params(":id");
		
		Organization organization = ServiceClient.getInstance()
				.organization()
				.get(id);
		
		Template template = Template.builder()
				.configuration(configuration)
				.controllerClass(OrganizationController.class)
				.putModel("organization", organization)
				.request(request)
				.templateName(Templates.ORGANIZATION)
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
		
		Organization organization = ServiceClient.getInstance()
				.organization()
				.get(id);
		
		Optional<Transaction> optional = organization.getTransactions()
				.stream()
				.filter(t -> t.getId().equals(invoiceNumber))
				.findFirst();
		
		if (optional.isPresent()) {
			
			Transaction transaction = optional.get();
			
			try {
				
				byte[] data = createInvoice(transaction);
				
				HttpServletResponse httpServletResponse = response.raw();
		        httpServletResponse.setContentType("application/pdf");
		        httpServletResponse.addHeader("Content-Disposition", String.format("inline; filename=invoice_%s.pdf", invoiceNumber));
		        httpServletResponse.getOutputStream().write(data);
		        httpServletResponse.getOutputStream().close();
				
			} catch (IOException e) {
				e.printStackTrace();
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
	
	private static byte[] createInvoice(Transaction transaction) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		
		PDDocument document = new PDDocument();
		PDPage page = new PDPage();
		document.addPage( page );

		// Create a new font object selecting one of the PDF base fonts
		PDFont font = PDType1Font.HELVETICA_BOLD;

		// Start a new content stream which will "hold" the to be created content
		PDPageContentStream contentStream = new PDPageContentStream(document, page);

		// Define a text content stream using the selected font, moving the cursor and drawing the text "Hello World"
		contentStream.beginText();
		contentStream.setFont( font, 12 );
		//contentStream.moveTo( 100, 700 );
		contentStream.showText( "Hello World" );
		contentStream.endText();

		// Make sure that the content stream is closed:
		contentStream.close();

		// Save the results and ensure that the document is properly closed:
		document.save( baos );
		document.close();
		
		return baos.toByteArray();
	}
}