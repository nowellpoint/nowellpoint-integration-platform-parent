package com.nowellpoint.console.service.impl;

import java.io.IOException;
import java.time.Instant;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;

import org.bson.types.ObjectId;
import org.mongodb.morphia.query.Query;

import com.braintreegateway.BraintreeGateway;
import com.braintreegateway.Environment;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.nowellpoint.console.entity.Address;
import com.nowellpoint.console.model.CreditCard;
import com.nowellpoint.console.entity.IdentityDAO;
import com.nowellpoint.console.model.Organization;
import com.nowellpoint.console.model.Subscription;
import com.nowellpoint.console.entity.OrganizationDAO;
import com.nowellpoint.console.model.Transaction;
import com.nowellpoint.console.model.Identity;
import com.nowellpoint.console.model.IdentityRequest;
import com.nowellpoint.console.service.AbstractService;
import com.nowellpoint.console.service.IdentityService;
import com.nowellpoint.console.service.ServiceClient;
import com.nowellpoint.console.util.UserContext;
import com.okta.sdk.authc.credentials.TokenClientCredentials;
import com.okta.sdk.client.Client;
import com.okta.sdk.client.Clients;
import com.okta.sdk.resource.user.User;
import com.okta.sdk.resource.user.UserBuilder;
import com.sendgrid.Content;
import com.sendgrid.Email;
import com.sendgrid.Mail;
import com.sendgrid.Method;
import com.sendgrid.Personalization;
import com.sendgrid.Request;
import com.sendgrid.SendGrid;

public class IdentityServiceImpl extends AbstractService implements IdentityService {
	
	private static final Logger LOGGER = Logger.getLogger(IdentityServiceImpl.class.getName());
	
	private static BraintreeGateway gateway = new BraintreeGateway(
			Environment.parseEnvironment(System.getenv("BRAINTREE_ENVIRONMENT")),
			System.getenv("BRAINTREE_MERCHANT_ID"),
			System.getenv("BRAINTREE_PUBLIC_KEY"),
			System.getenv("BRAINTREE_PRIVATE_KEY")
	);
	
	static {
		gateway.clientToken().generate();
	}
	
	private static Client client;
	
	static {
		client = Clients.builder()
	    		.setClientCredentials(new TokenClientCredentials(System.getenv("OKTA_API_KEY")))
	    		.setOrgUrl(System.getenv("OKTA_ORG_URL"))
	    		.build();	
	}
	
	private static final SendGrid sendgrid = new SendGrid(System.getenv("SENDGRID_API_KEY"));
	
	private IdentityDAO identityDAO;
	private OrganizationDAO organizationDAO;
	
	public IdentityServiceImpl() {
		identityDAO = new IdentityDAO(com.nowellpoint.console.entity.Identity.class, datastore);
		organizationDAO = new OrganizationDAO(com.nowellpoint.console.entity.Organization.class, datastore);
	}
	
	@Override
	public Identity create(IdentityRequest request) {
		
		User user = UserBuilder.instance()
				.setEmail(request.getEmail())
				.setLogin(request.getEmail())
				.setFirstName(request.getFirstName())
				.setLastName(request.getLastName())
				.setPassword(request.getPassword())
				.setActive(Boolean.FALSE)
				.addGroup(System.getenv("OKTA_DEFAULT_GROUP_ID"))
				.buildAndCreate(client);
		
		Identity identity = Identity.builder()
				.email(request.getEmail())
				.firstName(request.getFirstName())
				.lastName(request.getLastName())
				.locale(request.getLocale())
				.subject(user.getId())
				.status(user.getStatus().name())
				.build();
		
		sendVerifyEmailMessage(identity.getName(), identity.getEmail(), identity.getSubject());
		
		return create(identity);
	}

	@Override
	public Identity get(String id) {
		
		com.nowellpoint.console.entity.Identity entity = getEntry(id);
		
		if (entity == null) {
			
			try {
				entity = identityDAO.get(new ObjectId(id));
			} catch (IllegalArgumentException e) {
				throw new BadRequestException(String.format("Invalid Identity Id: %s", id));
			}
			
			if (entity == null) {
				throw new NotFoundException(String.format("Identity Id: %s was not found", id));
			}
			
			putEntry(entity.getId().toString(), entity);
		}
		
		return Identity.of(entity);
	}
	
	@Override
	public Identity getBySubject(String subject) {
		
		Identity identity = queryBySubject2(subject);
		
		Organization organization = ServiceClient.getInstance()
				.organization()
				.get(identity.getOrganization().getId());
		
		com.braintreegateway.Subscription source = gateway.subscription().find(organization.getSubscription().getNumber());
		
		Set<Transaction> transactions = new HashSet<>();
		
		source.getTransactions().forEach(t -> {
			transactions.add(Transaction.of(t));
		});
		
		Subscription subscription = Subscription.builder()
				.from(organization.getSubscription())
				.addAllTransactions(transactions)
				.billingPeriodEndDate(source.getBillingPeriodEndDate().getTime())
				.billingPeriodStartDate(source.getBillingPeriodStartDate().getTime())
				.nextBillingDate(source.getNextBillingDate().getTime())
				.build();
		
		ServiceClient.getInstance().organization().update(organization.getId(), subscription);
		
//		organization.getSubscription().setBillingPeriodEndDate(subscription.getBillingPeriodEndDate().getTime());
//		organization.getSubscription().setBillingPeriodStartDate(subscription.getBillingPeriodStartDate().getTime());
//		organization.getSubscription().setNextBillingDate(subscription.getNextBillingDate().getTime());
//		organization.setTransactions(transactions);
		
//		organizationDAO.save(entity.getOrganization());
		
//		putEntry(entity.getId().toString(), entity);
		
//		ServiceClient.getInstance().organization().u
		
		return identity;
	}
	
	public Identity activate(String activationToken) {
		
		Identity instance = queryBySubject2(activationToken);
		
		User user = activateUser(instance.getSubject());
		
		Identity identity = Identity.builder()
				.from(instance)
				.active(Boolean.TRUE)
				.status(user.getStatus().name())
				.build();
		
		return update(identity);
	}
	
	public Identity resendActivationEmail(String id) {
		Identity identity = get(id);
		
		sendVerifyEmailMessage(
				identity.getName(), 
				identity.getEmail(), 
				identity.getSubject());
		
		return identity;
	}
	
	public Identity deactivate(String id) {
		Identity instance = get(id);
		
		User user = deactivateUser(instance.getSubject());
		
		Identity identity = Identity.builder()
				.from(instance)
				.active(Boolean.FALSE)
				.status(user.getStatus().name())
				.build();
		
		return update(identity);
	}
	
	public void delete(String id) {
		Identity identity = get(id);
		deleteUser(identity.getSubject());
		delete(identity);
		removeEntry(id);
	}
	
	private void deleteUser(String userId) {
		User user = client.getUser(userId);
		user.delete();
	}
	
	private User deactivateUser(String userId) {
		User user = client.getUser(userId);
		user.activate(Boolean.FALSE);
		return user;
	}
	
	private User activateUser(String userId) {
		User user = client.getUser(userId);
		user.activate(Boolean.TRUE);
		return user;
	}
	
	private Identity create(Identity identity) {
		com.nowellpoint.console.entity.Identity entity = modelMapper.map(identity, com.nowellpoint.console.entity.Identity.class);
		entity.setCreatedBy(getSystemAdmin());
		entity.setCreatedOn(getCurrentDateTime());
		entity.setLastUpdatedBy(entity.getCreatedBy());
		entity.setLastUpdatedOn(entity.getCreatedOn());
		identityDAO.save(entity);
		return Identity.of(entity);
	}
	
	private Identity update(Identity identity) {
		com.nowellpoint.console.entity.Identity entity = modelMapper.map(identity, com.nowellpoint.console.entity.Identity.class);
		entity.setLastUpdatedOn(getCurrentDateTime());
		entity.setLastUpdatedBy(UserContext.get().getId());
		identityDAO.save(entity);
		return Identity.of(entity);
	}
	
	private void delete(Identity identity) {
		com.nowellpoint.console.entity.Identity entity = modelMapper.map(identity, com.nowellpoint.console.entity.Identity.class);
		identityDAO.delete(entity);
	}
	
	private void sendVerifyEmailMessage(String name, String email, String subject) {
		Executors.newSingleThreadExecutor().execute(new Runnable() {
			@Override
			public void run() {
				Email from = new Email();
				from.setEmail("administrator@nowellpoint.com");
				from.setName("Nowellpoint Support");
			    
			    Email to = new Email();
			    to.setEmail(email);
			    to.setName(name);
			    
			    Content content = new Content();
			    content.setType("text/html");
			    content.setValue("<html><body>some text here</body></html>");
			    
			    DynamicTemplatePersonalization personalization = new DynamicTemplatePersonalization();
			    personalization.addTo(to);
			    personalization.addDynamicTemplateData("name", name);
			    personalization.addDynamicTemplateData("subject", subject);
			    
			    Mail mail = new Mail();
			    mail.setFrom(from);
			    mail.addContent(content);
			    mail.setTemplateId("d-d709e41c60714b1bbac5491b33421e0f");
			    mail.addPersonalization(personalization);
			    
			    Request request = new Request();
			    try {
			    	request.setMethod(Method.POST);
			    	request.setEndpoint("mail/send");
			    	request.setBody(mail.build());
			    	sendgrid.api(request);
			    } catch (IOException e) {
			    	LOGGER.severe(e.getMessage());
			    }
			}
		});
	}
	
	private Identity queryBySubject2(String subject) {
		
		Query<com.nowellpoint.console.entity.Identity> query = identityDAO.createQuery()
				.field("subject")
				.equal(subject);
		
		com.nowellpoint.console.entity.Identity entity = identityDAO.findOne(query);
		
		if (entity == null) {
			throw new NotFoundException(String.format("Identity was not found for subject: %s", subject));
		}
		
		return Identity.of(entity);
	}
	
	private com.nowellpoint.console.entity.Identity queryBySubject(String subject) {
		
		Query<com.nowellpoint.console.entity.Identity> query = identityDAO.createQuery()
				.field("subject")
				.equal(subject);
		
		com.nowellpoint.console.entity.Identity entity = identityDAO.findOne(query);
		
		if (entity == null) {
			throw new NotFoundException(String.format("Identity was not found for subject: %s", subject));
		}
		
		return entity;
	}
	
	private static class DynamicTemplatePersonalization extends Personalization {

        @JsonProperty(value = "dynamic_template_data")
        private Map<String, Object> dynamicTemplateData;

        @JsonProperty("dynamic_template_data")
        public Map<String, Object> getDynamicTemplateData() {
            if (dynamicTemplateData == null) {
                return Collections.<String, Object>emptyMap();
            }
            return dynamicTemplateData;
        }

        public void addDynamicTemplateData(String key, Object value) {
            if (dynamicTemplateData == null) {
                dynamicTemplateData = new HashMap<String, Object>();
                dynamicTemplateData.put(key, value);
            } else {
                dynamicTemplateData.put(key, value);
            }
        }

    }
}
