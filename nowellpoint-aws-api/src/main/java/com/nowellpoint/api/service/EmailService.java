package com.nowellpoint.api.service;

public interface EmailService {
	
	public void sendEmailVerificationMessage(String email, String name, String emailVerificationToken);
	
	public void sendWelcomeMessage(String email, String username, String name);
	
	public void sendInvoiceMessage(String email, String name, String invoiceNumber, String base64EncodedContent);
}