package com.nowellpoint.api.service;

import java.net.URI;

public interface EmailService {
	
	public void sendEmailVerificationMessage(String email, String name, URI emailVerificationHref);
	public void sendEmailVerificationMessage(String email, String name, String emailVerificationToken);
	public void sendWelcomeMessage(String email, String username, String name);
	public void sendInvoiceMessage(String email, String name, String invoiceNumber, String invoiceLink, String base64EncodedContent);
}