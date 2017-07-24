package com.nowellpoint.api.service;

public interface EmailService {
	public void sendEmailVerificationMessage(String email, String name, String emailVerificationToken);
	public void sendWelcomeMessage(String email, String username, String name, String temporaryPassword);
	public void sendInvoiceMessage(String email, String name, String invoiceNumber, String invoiceLink, String base64EncodedContent);
}