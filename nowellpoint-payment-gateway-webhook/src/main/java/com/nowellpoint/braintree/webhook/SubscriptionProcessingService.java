package com.nowellpoint.braintree.webhook;

import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.Locale;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.events.DynamodbEvent;
import com.braintreegateway.BraintreeGateway;
import com.braintreegateway.Customer;
import com.braintreegateway.Environment;
import com.braintreegateway.Plan;
import com.braintreegateway.Subscription;
import com.braintreegateway.Transaction;
import com.braintreegateway.WebhookNotification;
import com.nowellpoint.payables.invoice.InvoiceGenerator;
import com.nowellpoint.payables.invoice.model.Invoice;
import com.nowellpoint.payables.invoice.model.Payee;
import com.nowellpoint.payables.invoice.model.PaymentMethod;
import com.nowellpoint.payables.invoice.model.SendEmailRequest;
import com.nowellpoint.payables.invoice.model.Service;
import com.sendgrid.Attachments;
import com.sendgrid.Content;
import com.sendgrid.Email;
import com.sendgrid.Mail;
import com.sendgrid.Method;
import com.sendgrid.Personalization;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;

public class SubscriptionProcessingService {
	
	private static final DynamoDBMapper mapper = DynamoDBMapperProvider.getDynamoDBMapper();
	private LambdaLogger logger;
	
	public String handleEvent(DynamodbEvent event, Context context) {
    	
    	logger = context.getLogger();
		
    	event.getRecords().stream().filter(record -> "INSERT".equals(record.getEventName())).forEach(record -> {
    		
    		logger.log("DynamodbEvent received...Event Id: "
					.concat(record.getEventID())
					.concat(" Event Name: " + record.getEventName()));
    		
    		String id = record.getDynamodb().getKeys().get("Id").getS();
    		
    		PaymentGatewayNotification notification = mapper.load(PaymentGatewayNotification.class, id);
    		
    		logger.log(notification.getMerchantId());
    		
    		notification.setStatus(PaymentGatewayNotification.Status.PROCESSED.name());
    		
    		try {
    			
    			BraintreeGateway gateway = new BraintreeGateway(
        				Environment.parseEnvironment(notification.getEnvironment()),
        				notification.getMerchantId(),
        				notification.getPublicKey(),
        				notification.getPrivateKey()
        		);
        		
        		gateway.clientToken().generate();
        		
        		Subscription subscription = gateway.subscription().find(notification.getSubscriptionId());
    			
    			Transaction transaction = subscription.getTransactions()
    					.stream().sorted((t1,t2) -> t2.getCreatedAt().compareTo(t1.getCreatedAt()))
    					.findFirst()
    					.get();
        		
    			/**
    			 * 
    			 */
    			
    			if (notification.getWebhookNotificationKind().equals(WebhookNotification.Kind.SUBSCRIPTION_CHARGED_SUCCESSFULLY.name())) {
    				
    				List<Plan> plans = gateway
    						.plan()
    						.all();
    				
    				Plan plan = plans.stream()
    						.filter(p -> p.getId().equals(transaction.getPlanId()))
    						.findFirst()
    						.get();
    				
    				Customer customer = gateway
    						.customer()
    						.find(transaction.getCustomer().getId());
    				
    				Payee payee = Payee.builder()
    						.customerId(customer.getId())
    						.attentionTo(transaction.getCreditCard().getCardholderName())
    						.email(customer.getEmail())
    						.city(customer.getAddresses().get(0).getLocality())
    						.companyName(customer.getCompany())
    						.country(customer.getAddresses().get(0).getCountryName())
    						.postalCode(customer.getAddresses().get(0).getPostalCode())
    						.state(customer.getAddresses().get(0).getRegion())
    						.street(customer.getAddresses().get(0).getStreetAddress())
    						.build();
    				
    				PaymentMethod paymentMethod = PaymentMethod.builder()
    						.cardType(transaction.getCreditCard().getCardType())
    						.lastFour(transaction.getCreditCard().getLast4())
    						.build();
    				
    				Service service = Service.builder()
    						.quantity(1)
    						.serviceName(plan.getName())
    						.totalPrice(transaction.getAmount())
    						.unitPrice(plan.getPrice())
    						.build();
    				
    				Invoice invoice = Invoice.builder()
    						.billingPeriodEndDate(subscription.getBillingPeriodEndDate().getTime())
    						.billingPeriodStartDate(subscription.getBillingPeriodStartDate().getTime())
    						.invoiceNumber(transaction.getId())
    						.locale(Locale.US)
    						.transactionDate(transaction.getCreatedAt().getTime())
    						.payee(payee)
    						.paymentMethod(paymentMethod)
    						.addServices(service)
    						.build();
    				
    				byte[] bytes = new InvoiceGenerator().generate(invoice);
    				
    				SendEmailRequest request = SendEmailRequest.builder()
    						.apiKey(notification.getEmailApiKey())
    						.customerId(invoice.getPayee().getCustomerId())
    						.encodedContent(Base64.getEncoder().encodeToString(bytes))
    						.invoiceNumber(invoice.getInvoiceNumber())
    						.name(invoice.getPayee().getAttentionTo())
    						.to(invoice.getPayee().getEmail())
    						.applicationHostname(notification.getApplicationHostname())
    						.build();
    				
    				sendInvoice(request);
    			}
    			
    		} catch (Exception e) {
    			notification.setStatus(PaymentGatewayNotification.Status.FAILED.name());
    			notification.setErrorMessage(e.getMessage());
    		} finally {
    			mapper.save(notification);
    		}
    	});
		
    	return "ok";
	}
	
	private void sendInvoice(SendEmailRequest request) throws IOException {
		Email from = new Email();
		from.setEmail("payables@nowellpoint.com");
		from.setName("Nowellpoint Payables");
	    
	    Email to = new Email();
	    to.setEmail(request.getTo());
	    to.setName(request.getName());
	    
	    Content content = new Content();
	    content.setType("text/html");
	    content.setValue("<html><body>some text here</body></html>");
	    	    
	    Personalization personalization = new Personalization();
	    personalization.addTo(to);
	    personalization.addSubstitution("%name%", request.getName());
	    personalization.addSubstitution("%invoice-link%", String.format("%s/app/account-profile/%s/current-plan", request.getApplicationHostname(), request.getCustomerId()));
	    
	    Attachments attachments = new Attachments();
	    attachments.setContent(request.getEncodedContent());
	    attachments.setType("application/pdf");
	    attachments.setFilename(String.format("invoice_%s.pdf", request.getInvoiceNumber()));
	    attachments.setDisposition("attachment");
	    attachments.setContentId("Invoice");
	    
	    Mail mail = new Mail();
	    mail.setFrom(from);
	    mail.addContent(content);
	    mail.setTemplateId("78e36394-86c3-4e16-be73-a3ed3ddae1a8");
	    mail.addPersonalization(personalization);
	    mail.addAttachments(attachments);
	    
	    sendEmail(mail, request.getApiKey());
	}
	
	private void sendEmail(Mail mail, String apiKey) throws IOException {
		SendGrid sendgrid = new SendGrid(apiKey);
	    
	    Request request = new Request();
	    request.method = Method.POST;
	    request.endpoint = "mail/send";
	    request.body = mail.build();
	    
	    Response response = sendgrid.api(request);
	    
	    logger.log("sendInvoiceMessage: " + response.statusCode + " : " + response.body);	
	}
}