package com.nowellpoint.console.webdriver;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.nowellpoint.console.model.Identity;
import com.nowellpoint.console.service.ServiceClient;

@RunWith(JUnit4.class)
public class TestSignUp {
	private static ChromeDriverService service;
	private WebDriver driver;

	@BeforeClass
	public static void createAndStartService() {
		service = new ChromeDriverService.Builder()
				.usingDriverExecutable(new File("/usr/local/bin/chromedriver"))
				.usingAnyFreePort()
				.build();
		
		try {
			service.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@AfterClass
	public static void createAndStopService() {
		service.stop();
	}

	@Before
	public void createDriver() {
		driver = new RemoteWebDriver(service.getUrl(), new ChromeOptions());
	}

	@After
	public void quitDriver() {
		driver.quit();
	}

	@Test
	public void testGoogleSearch() {
		driver.get("https://localhost:8443/free/");
		
		WebElement firstName = driver.findElement(By.name("firstName"));
		WebElement lastName = driver.findElement(By.name("lastName"));
		WebElement email = driver.findElement(By.name("email"));
		WebElement form = driver.findElement(By.id("signup-form"));
		
		String emailAddress = String.format("%s@s0nny.com", RandomStringUtils.random(8, true, false));
		
		System.out.println(emailAddress);
		
		firstName.sendKeys("Selenium");
		lastName.sendKeys("Test");
		email.sendKeys(emailAddress);
		
		form.submit();
		
		WebDriverWait wait = new WebDriverWait(driver, 10);
	    Boolean activationPage = wait.until(
	           ExpectedConditions.urlContains("/activate/")
	    );
	    
	    assertTrue(activationPage);
	    
	    Identity identity = ServiceClient.getInstance()
	    		.identity()
	    		.getByUsername(emailAddress);
	    
	    assertNotNull(identity.getId());
	    
	    ServiceClient.getInstance().identity().delete(identity.getId());
	    
	    ServiceClient.getInstance().organization().delete(identity.getOrganization().getId());
	}
	
//	private void readEmail(String emailAddress) {
//		
//		String hash = DigestUtils.md5Hex(emailAddress);
//		
//		System.out.println(hash);
//		
//		HttpResponse response = RestResource.get(String.format("https://privatix-temp-mail-v1.p.mashape.com/request/one_mail/id/%s/", hash))
//				.acceptCharset(StandardCharsets.UTF_8)
//				.accept(MediaType.APPLICATION_JSON)
//				.header("X-Mashape-Key", "R0d7IxAJNzmsh4ue84KhWnMarKUDp1xZNvEjsnWHPaUw27abkm")
//				.execute();
//		
//		ObjectMapper mapper = new ObjectMapper();
//		
//		try {
//			JsonNode mail = mapper.readTree(response.getAsString());
//			
//			System.out.println(mail.toString());
//			
//		} catch (JsonProcessingException e) {
//			e.printStackTrace();
//		} catch (HttpRequestException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}
}