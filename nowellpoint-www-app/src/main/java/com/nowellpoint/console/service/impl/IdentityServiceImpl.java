package com.nowellpoint.console.service.impl;

import java.io.IOException;
import java.util.Collections;
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
import com.nowellpoint.console.entity.IdentityDAO;
import com.nowellpoint.console.model.Identity;
import com.nowellpoint.console.model.IdentityRequest;
import com.nowellpoint.console.model.Organization;
import com.nowellpoint.console.model.OrganizationInfo;
import com.nowellpoint.console.model.SubscriptionRequest;
import com.nowellpoint.console.model.Transaction;
import com.nowellpoint.console.service.AbstractService;
import com.nowellpoint.console.service.IdentityService;
import com.nowellpoint.console.service.ServiceClient;
import com.nowellpoint.console.util.UserContext;
import com.nowellpoint.util.SecretsManager;
import com.okta.sdk.authc.credentials.TokenClientCredentials;
import com.okta.sdk.client.Client;
import com.okta.sdk.client.Clients;
import com.okta.sdk.resource.user.PasswordCredential;
import com.okta.sdk.resource.user.User;
import com.okta.sdk.resource.user.UserBuilder;
import com.okta.sdk.resource.user.UserCredentials;
import com.okta.sdk.resource.user.UserProfile;
import com.sendgrid.Content;
import com.sendgrid.Email;
import com.sendgrid.Mail;
import com.sendgrid.Method;
import com.sendgrid.Personalization;
import com.sendgrid.Request;
import com.sendgrid.SendGrid;

public class IdentityServiceImpl extends AbstractService implements IdentityService {
	
	private static final Logger LOGGER = Logger.getLogger(IdentityServiceImpl.class.getName());
	
	private static final BraintreeGateway gateway = new BraintreeGateway(
			Environment.parseEnvironment(SecretsManager.getBraintreeEnvironment()),
			SecretsManager.getBraintreeMerchantId(),
			SecretsManager.getBraintreePublicKey(),
			SecretsManager.getBraintreePrivateKey()
	);
	
	private static final Client client = Clients.builder()
	    		.setClientCredentials(new TokenClientCredentials(SecretsManager.getOktaApiKey()))
	    		.setOrgUrl(SecretsManager.getOktaOrgUrl())
	    		.build();	
	
	private static final SendGrid sendgrid = new SendGrid(SecretsManager.getSendGridApiKey());
	
	private IdentityDAO dao;
	
	public IdentityServiceImpl() {
		dao = new IdentityDAO(com.nowellpoint.console.entity.Identity.class, datastore);
	}
	
	@Override
	public Identity create(IdentityRequest request) {
		
		User user = UserBuilder.instance()
				.setEmail("administrator@nowellpoint.com")
				.setLogin(request.getEmail())
				.setFirstName(request.getFirstName())
				.setLastName(request.getLastName())
				.setPassword(request.getPassword().toCharArray())
				.setActive(Boolean.FALSE)
				.addGroup(SecretsManager.getOktaDefaultGroupId())
				.buildAndCreate(client);
		
		Identity identity = Identity.builder()
				.email(request.getEmail())
				.firstName(request.getFirstName())
				.lastName(request.getLastName())
				.locale(request.getLocale())
				.organization(OrganizationInfo.builder()
						.id(request.getOrganizationId())
						.build())
				.timeZone(request.getTimeZone())
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
				entity = dao.get(new ObjectId(id));
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
		
		Identity identity = queryBySubject(subject);
		
		Organization organization = ServiceClient.getInstance()
				.organization()
				.get(identity.getOrganization().getId());
		
		if (! "FREE".equals(organization.getSubscription().getPlanCode())) {
			
			com.braintreegateway.Subscription source = gateway.subscription().find(organization.getSubscription().getNumber());
			
			Set<Transaction> transactions = new HashSet<>();
			
			source.getTransactions().forEach(transaction -> {
				transactions.add(Transaction.of(transaction));
			});
			
			SubscriptionRequest request = SubscriptionRequest.builder()
					.billingPeriodEndDate(source.getBillingPeriodEndDate().getTime())
					.billingPeriodStartDate(source.getBillingPeriodStartDate().getTime())
					.nextBillingDate(source.getNextBillingDate().getTime())
					.transactions(transactions)
					.build();
			
			@SuppressWarnings("unused")
			Organization instance = ServiceClient.getInstance()
					.organization()
					.update(organization.getId(), request);
			
		}
		
		return identity;
	}
	
	@Override
	public Identity getByUsername(String username) {
		return queryByUsername(username);
	}
	
	@Override
	public Identity activate(String activationToken) {
		
		Identity instance = getBySubject(activationToken);
		
		User user = activateUser(instance.getSubject());
		
		Identity identity = Identity.builder()
				.from(instance)
				.active(Boolean.TRUE)
				.status(user.getStatus().name())
				.build();
		
		return update(identity);
	}
	
	@Override
	public Identity setPassword(String id, char[] password) {
		
		Identity instance = get(id);
		
		PasswordCredential passwordCredential = client.instantiate(PasswordCredential.class)
				.setValue(password);
		
		UserCredentials userCredentials = client.instantiate(UserCredentials.class)
				.setPassword(passwordCredential);
		
		User user = client.getUser(instance.getSubject());
		UserProfile userProfile = user.getProfile();
		
		userProfile.setEmail(instance.getEmail());
		
		user.setProfile(userProfile);
		user.setCredentials(userCredentials);
		user.update();
		
		Identity identity = Identity.builder()
				.from(instance)
				.status(user.getStatus().name())
				.build();
		
		return update(identity);
	}
	
	@Override
	public Identity update(String id, IdentityRequest request) {
		Identity instance = get(id);
		
		User user = client.getUser(instance.getSubject());
		
		UserProfile userProfile = user.getProfile()
				.setEmail(request.getEmail())
				.setLogin(request.getEmail())
				.setFirstName(request.getFirstName())
				.setLastName(request.getLastName());
		
		user.setProfile(userProfile);
		user.update();
		
		Identity identity = Identity.builder()
				.from(instance)
				.email(request.getEmail())
				.firstName(request.getFirstName())
				.lastName(request.getLastName())
				.locale(request.getLocale())
				.timeZone(request.getTimeZone())
				.subject(user.getId())
				.status(user.getStatus().name())
				.build();
		
		return update(identity);
	}
	
	@Override
	public Identity resendActivationEmail(String id) {
		Identity identity = get(id);
		
		sendVerifyEmailMessage(
				identity.getName(), 
				identity.getEmail(), 
				identity.getSubject());
		
		return identity;
	}
	
	@Override
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
	
	@Override
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
		return client.getUser(userId);
	}
	
	private User activateUser(String userId) {
		User user = client.getUser(userId);
		user.activate(Boolean.TRUE);
		return client.getUser(userId);
	}
	
	private Identity create(Identity identity) {
		com.nowellpoint.console.entity.Identity entity = modelMapper.map(identity, com.nowellpoint.console.entity.Identity.class);
		entity.setCreatedBy(new com.nowellpoint.console.entity.Identity(UserContext.get() != null ? UserContext.get().getId() : getSystemAdmin().getId().toString()));
		entity.setCreatedOn(getCurrentDateTime());
		entity.setLastUpdatedBy(entity.getCreatedBy());
		entity.setLastUpdatedOn(entity.getCreatedOn());
		dao.save(entity);
		entity = dao.get(entity.getId());
		putEntry(entity.getId().toString(), entity);
		return Identity.of(entity);
	}
	
	private Identity update(Identity identity) {
		com.nowellpoint.console.entity.Identity entity = modelMapper.map(identity, com.nowellpoint.console.entity.Identity.class);
		entity.setLastUpdatedOn(getCurrentDateTime());
		entity.setLastUpdatedBy(new com.nowellpoint.console.entity.Identity(UserContext.get() != null ? UserContext.get().getId() : getSystemAdmin().getId().toString()));
		dao.save(entity);
		entity = dao.get(entity.getId());
		putEntry(entity.getId().toString(), entity);
		return Identity.of(entity);
	}
	
	private void delete(Identity identity) {
		com.nowellpoint.console.entity.Identity entity = modelMapper.map(identity, com.nowellpoint.console.entity.Identity.class);
		dao.delete(entity);
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
	
	private Identity queryBySubject(String subject) {
		
		Query<com.nowellpoint.console.entity.Identity> query = dao.createQuery()
				.field("subject")
				.equal(subject);
		
		com.nowellpoint.console.entity.Identity entity = dao.findOne(query);
		
		if (entity == null) {
			throw new NotFoundException(String.format("Identity was not found for subject: %s", subject));
		}
		
		putEntry(entity.getId().toString(), entity);
		
		return Identity.of(entity);
	}
	
	private Identity queryByUsername(String username) {
		
		Query<com.nowellpoint.console.entity.Identity> query = dao.createQuery()
				.field("username")
				.equal(username);
		
		com.nowellpoint.console.entity.Identity entity = dao.findOne(query);
		
		if (entity == null) {
			throw new NotFoundException(String.format("Identity was not found for username: %s", username));
		}
		
		putEntry(entity.getId().toString(), entity);
		
		return Identity.of(entity);
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
