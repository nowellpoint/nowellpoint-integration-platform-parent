package com.nowellpoint.aws.api.resource;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import javax.annotation.security.PermitAll;
import javax.validation.constraints.Pattern;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.Response.Status;

import org.bson.types.ObjectId;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

import com.nowellpoint.aws.api.model.AccountProfile;
import com.nowellpoint.aws.api.model.Address;
import com.nowellpoint.aws.api.model.sforce.Lead;
import com.nowellpoint.aws.api.tasks.AccountProfileSetupTask;
import com.nowellpoint.aws.api.tasks.AccountSetupTask;
import com.nowellpoint.aws.api.tasks.SubmitLeadTask;
import com.nowellpoint.aws.data.MongoDBDatastore;
import com.nowellpoint.aws.idp.model.Account;
import com.nowellpoint.aws.model.admin.Properties;
import com.stormpath.sdk.account.AccountStatus;

@Path("/signup")
public class SignUpService {
	
	@Context
	private UriInfo uriInfo;

	@PermitAll
	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
    public Response signUp(
    		@FormParam("leadSource") @NotEmpty(message = "Lead Source must be filled in") String leadSource,
    		@FormParam("firstName") String firstName,
    		@FormParam("lastName") @NotEmpty(message="Last Name must be filled in") String lastName,
    		@FormParam("email") @Email String email,
    		@FormParam("countryCode") @NotEmpty String countryCode,
    		@FormParam("password") @Length(min=8, max=100, message="Password must be between {min} and {max}") @Pattern.List({
    	        @Pattern(regexp = "(?=.*[0-9]).+", message = "Password must contain one digit."),
    	        @Pattern(regexp = "(?=.*[a-z]).+", message = "Password must contain one lowercase letter."),
    	        @Pattern(regexp = "(?=.*[a-z]).+", message = "Password must contain one upper letter."),
    	        @Pattern(regexp = "(?=.*[!@#$%^&*+=?-_()/\"\\.,<>~`;:]).+", message ="Password must contain one special character."),
    	        @Pattern(regexp = "(?=\\S+$).+", message = "Password must contain no whitespace.") }) String password) {
		
		Lead lead = new Lead();
		lead.setLeadSource(leadSource);
		lead.setFirstName(firstName);
		lead.setLastName(lastName);
		lead.setEmail(email);
		lead.setCountryCode(countryCode);
		
		Account account = new Account();
		account.setGivenName(firstName);
		account.setMiddleName(null);
		account.setSurname(lastName);
		account.setEmail(email);
		account.setUsername(email);
		account.setPassword(password);
		account.setStatus("UNVERIFIED");
		
		/**
		 * ccount.setGivenName("Joe")
                .setSurname("Quickstart_Stormtrooper")
                .setUsername("tk421")  
                .setEmail("tk421@stormpath.com")
                .setPassword("Changeme1")
                .setStatus(AccountStatus.UNVERIFIED);
		 */
		
		AccountProfile accountProfile = new AccountProfile();
		accountProfile.setCreatedById(System.getProperty(Properties.DEFAULT_SUBJECT));
		accountProfile.setLastModifiedById(System.getProperty(Properties.DEFAULT_SUBJECT));
		accountProfile.setFirstName(firstName);
		accountProfile.setLastName(lastName);
		accountProfile.setEmail(email);
		accountProfile.setUsername(email);
		accountProfile.setIsActive(Boolean.TRUE);
		
		Address address = new Address();
		address.setCountryCode(countryCode);
		
		accountProfile.setAddress(address);
		
		Set<Callable<String>> tasks = new LinkedHashSet<Callable<String>>();
		tasks.add(new SubmitLeadTask(lead));
		tasks.add(new AccountProfileSetupTask(accountProfile));
		
		ExecutorService executor = Executors.newFixedThreadPool(3);
		
		Future<Account> accountTask = executor.submit(new AccountSetupTask(account));
		
		try {
			List<Future<String>> futures = executor.invokeAll(tasks);
			executor.shutdown();
			executor.awaitTermination(30, TimeUnit.SECONDS);
			
			accountProfile.setLeadId(futures.get(0).get());
			System.out.println("1");
			accountProfile.setHref(accountTask.get().getHref());
			System.out.println("2");
			accountProfile.setId(new ObjectId(futures.get(1).get()));
			System.out.println("3");
		} catch (InterruptedException | ExecutionException e) {
			throw new WebApplicationException(e.getMessage(), Status.INTERNAL_SERVER_ERROR);
		}
		
		Executors.newSingleThreadExecutor().execute(new Runnable() {
			@Override
			public void run() {
				MongoDBDatastore.replaceOne(accountProfile);
			}
		});	
		
		Map<String,String> response = new HashMap<String,String>();
		response.put("id", accountProfile.getId().toString());
		response.put("leadId", accountProfile.getLeadId());
		response.put("href", accountProfile.getHref());
		
		return Response.ok(response).build();
	}
}