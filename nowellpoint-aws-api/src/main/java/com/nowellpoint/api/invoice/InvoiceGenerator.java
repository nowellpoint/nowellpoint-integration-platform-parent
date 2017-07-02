package com.nowellpoint.api.invoice;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.concurrent.Executors;

import org.jboss.logging.Logger;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;
import com.nowellpoint.api.invoice.model.Invoice;
import com.nowellpoint.api.invoice.model.InvoiceGeneratorException;
import com.nowellpoint.api.invoice.model.InvoiceProperties;
import com.nowellpoint.api.invoice.model.Payee;
import com.nowellpoint.api.invoice.model.PaymentMethod;
import com.nowellpoint.api.invoice.model.Service;
import com.nowellpoint.util.Properties;
import com.sendgrid.Attachments;
import com.sendgrid.Content;
import com.sendgrid.Email;
import com.sendgrid.Mail;
import com.sendgrid.Method;
import com.sendgrid.Personalization;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;

public class InvoiceGenerator {
	
	private static final Logger LOG = Logger.getLogger(InvoiceGenerator.class);
	private static final SimpleDateFormat sdf = new SimpleDateFormat("MMMM d, yyyy");
	
	private static Font HELVETICA_14_NORMAL_LIGHT_BLUE = new Font(Font.FontFamily.HELVETICA, 14, Font.NORMAL, new BaseColor(27, 150, 254));
	private static Font HELVETICA_10_NORMAL_LIGHT_BLUE = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL, new BaseColor(27, 150, 254));
    private static Font HELVETICA_10_NORMAL_GRAY = new Font(Font.FontFamily.HELVETICA, 10.0f, Font.NORMAL, new BaseColor(79,79,79));
    private static Font HELVETICA_10_BOLD_GRAY = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD, new BaseColor(79,79,79));
    
    private Locale locale;

	public void generate(Invoice invoice) {
		
		setLocale(invoice.getLocale());
		
		Document document = new Document();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
        	
			PdfWriter.getInstance(document, baos);
			
			document.setMargins(75, 75, 75, 10);
			document.addTitle(getLabel(InvoiceProperties.INVOICE_TITLE));
			document.addAuthor("Nowellpoint Invoice Generation Service");
			document.addSubject(String.format("Invoice for %s", invoice.getPayee().getCompanyName()));
			document.addCreator(InvoiceGenerator.class.getName());
			
			document.open();
			
			document.add(getHeader());
	        document.add(getCompany(invoice.getInvoiceNumber(), invoice.getTransactionDate()));
	        document.add(getPayee(invoice.getPayee()));
	        document.add(getBillingPeriod(invoice.getBillingPeriodStartDate(), invoice.getBillingPeriodEndDate()));
	        document.add(getServicesList(invoice.getServices()));
	        document.add(getPaymentMethod(invoice.getPaymentMethod()));
	        document.add(getSeparator());
	        
	        document.close();
	        
		} catch (DocumentException e) {
			LOG.error(e);
			throw new InvoiceGeneratorException(e);
		}
        
        putObject(invoice.getInvoiceNumber(), baos);
        
        System.out.println(invoice.getPayee().getEmail());
        
        sendInvoice(
        		invoice.getPayee().getCustomerId(), 
        		"john.d.herson@gmail.com", //invoice.getPayee().getEmail(), 
        		invoice.getPayee().getAttentionTo(), 
        		invoice.getInvoiceNumber(), 
        		Base64.getEncoder().encodeToString(baos.toByteArray()));
	}
	
	private void setLocale(Locale locale) {
		this.locale = locale;
	}
	
	private void putObject(String keyName, ByteArrayOutputStream baos) {
		byte[] bytes = baos.toByteArray();
		
		ObjectMetadata objectMetadata = new ObjectMetadata();
		objectMetadata.setContentLength(bytes.length);
		objectMetadata.setContentType("application/pdf");
		
		PutObjectRequest request = new PutObjectRequest(
				"nowellpoint-invoices",
				keyName,
				new ByteArrayInputStream(bytes),
				objectMetadata);
		
		AmazonS3 s3client = AmazonS3ClientBuilder.defaultClient();
		s3client.putObject(request);
	}
	
	private PdfPTable getBillingPeriod(Date billingPeriodStartDate, Date billingPeriodEndDate) {
		PdfPCell cell = new PdfPCell(new Phrase(String.format("Billing Period: %s - %s", sdf.format(billingPeriodStartDate), sdf.format(billingPeriodEndDate)), HELVETICA_10_NORMAL_LIGHT_BLUE));
	    cell.setPadding(10.0f);
	    cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
	    cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
	    cell.setBorderWidth(0.1f);
	    cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
        
		PdfPTable table = new PdfPTable(1);
        table.setWidthPercentage(100);
        table.setSpacingAfter(30);
        table.addCell(cell);
        return table;
	}
	
	private PdfPTable getPayee(Payee payee) {
		PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setSpacingAfter(30);
        
        table.addCell(getCell("Bill To", PdfPCell.ALIGN_LEFT, HELVETICA_10_NORMAL_LIGHT_BLUE));
        table.addCell(getCell("", PdfPCell.ALIGN_RIGHT, HELVETICA_10_NORMAL_LIGHT_BLUE));
        
        table.addCell(getCell(payee.getCompanyName(), PdfPCell.ALIGN_LEFT, HELVETICA_10_BOLD_GRAY));
        table.addCell(getCell("", PdfPCell.ALIGN_RIGHT, HELVETICA_10_NORMAL_GRAY));
        
        table.addCell(getCell(String.format("ATTN: %s", payee.getAttentionTo()), PdfPCell.ALIGN_LEFT, HELVETICA_10_NORMAL_GRAY));
        table.addCell(getCell("", PdfPCell.ALIGN_RIGHT, HELVETICA_10_NORMAL_GRAY));
        
        table.addCell(getCell(payee.getStreet(), PdfPCell.ALIGN_LEFT, HELVETICA_10_NORMAL_GRAY));
        table.addCell(getCell("", PdfPCell.ALIGN_RIGHT, HELVETICA_10_NORMAL_GRAY));
        
        table.addCell(getCell(String.format("%s, %s, %s", payee.getCity(), payee.getState(), payee.getPostalCode()), PdfPCell.ALIGN_LEFT, HELVETICA_10_NORMAL_GRAY));
        table.addCell(getCell("", PdfPCell.ALIGN_RIGHT, HELVETICA_10_NORMAL_GRAY));
        
        table.addCell(getCell(payee.getCountry(), PdfPCell.ALIGN_LEFT, HELVETICA_10_NORMAL_GRAY));
        table.addCell(getCell("", PdfPCell.ALIGN_RIGHT, HELVETICA_10_NORMAL_GRAY));
        
        return table;
	}
	
	private PdfPTable getHeader() {
		PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setSpacingAfter(30);
        table.addCell(getLogoCell());
        table.addCell(getInvoiceCell());
        
        return table;
	}
	
	private PdfPTable getCompany(String transactionId, Date transactionDate) {
		PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setSpacingAfter(30);
        
        table.addCell(getCell("Nowellpoint LLC", PdfPCell.ALIGN_LEFT, HELVETICA_10_NORMAL_LIGHT_BLUE));
        table.addCell(getCell("Issue Date", PdfPCell.ALIGN_RIGHT, HELVETICA_10_NORMAL_LIGHT_BLUE));
        
        table.addCell(getCell("129 S. Bloodworth Street", PdfPCell.ALIGN_LEFT, HELVETICA_10_NORMAL_GRAY));
        table.addCell(getCell(sdf.format(transactionDate), PdfPCell.ALIGN_RIGHT, HELVETICA_10_NORMAL_GRAY));
        
        table.addCell(getCell("Raleigh, NC 27601", PdfPCell.ALIGN_LEFT, HELVETICA_10_NORMAL_GRAY));
        table.addCell(getCell("", PdfPCell.ALIGN_RIGHT, HELVETICA_10_NORMAL_GRAY));
        
        table.addCell(getCell("United States", PdfPCell.ALIGN_LEFT, HELVETICA_10_NORMAL_GRAY));
        table.addCell(getCell("", PdfPCell.ALIGN_RIGHT, HELVETICA_10_NORMAL_LIGHT_BLUE));
        
        table.addCell(getCell("1-888-721-6440", PdfPCell.ALIGN_LEFT, HELVETICA_10_NORMAL_GRAY));
        table.addCell(getCell("Invoice No", PdfPCell.ALIGN_RIGHT, HELVETICA_10_NORMAL_LIGHT_BLUE));
        
        table.addCell(getCell("www.nowellpoint.com", PdfPCell.ALIGN_LEFT, HELVETICA_10_NORMAL_GRAY));
        table.addCell(getCell(transactionId, PdfPCell.ALIGN_RIGHT, HELVETICA_10_NORMAL_GRAY));
        
        return table;
	}
	
	private PdfPTable getServicesList(Set<Service> services) throws DocumentException {
		PdfPTable table = new PdfPTable(4);
		table.setWidthPercentage(100);
		table.setSpacingBefore(5);
		table.setSpacingAfter(10);
		table.setWidths(new int[]{3, 1, 1, 1});
        
		table.addCell(getHeaderItemCell("Plan", PdfPCell.ALIGN_LEFT, HELVETICA_10_NORMAL_LIGHT_BLUE));
		table.addCell(getHeaderItemCell("Unit", PdfPCell.ALIGN_RIGHT, HELVETICA_10_NORMAL_LIGHT_BLUE));
		table.addCell(getHeaderItemCell("Quantity", PdfPCell.ALIGN_RIGHT, HELVETICA_10_NORMAL_LIGHT_BLUE));
		table.addCell(getHeaderItemCell("Amount", PdfPCell.ALIGN_RIGHT, HELVETICA_10_NORMAL_LIGHT_BLUE));
		
		BigDecimal totalAmount = new BigDecimal(0.00);
		
		services.stream().forEach(service -> {
			table.addCell(getServiceCell(service.getServiceName(), PdfPCell.ALIGN_LEFT, HELVETICA_10_NORMAL_GRAY));
			table.addCell(getServiceCell(String.valueOf(service.getUnitPrice()), PdfPCell.ALIGN_RIGHT, HELVETICA_10_NORMAL_GRAY));
			table.addCell(getServiceCell(String.valueOf(service.getQuantity()), PdfPCell.ALIGN_RIGHT, HELVETICA_10_NORMAL_GRAY));
			table.addCell(getServiceCell(String.valueOf(service.getTotalPrice()), PdfPCell.ALIGN_RIGHT, HELVETICA_10_NORMAL_GRAY));
			
			totalAmount.add(service.getTotalPrice());
		});
        
		table.addCell(getServiceCell("Total", PdfPCell.ALIGN_LEFT, HELVETICA_10_NORMAL_GRAY));
		table.addCell(getServiceCell("", PdfPCell.ALIGN_RIGHT, HELVETICA_10_NORMAL_GRAY));
		table.addCell(getServiceCell("", PdfPCell.ALIGN_RIGHT, HELVETICA_10_NORMAL_GRAY));
		table.addCell(getServiceCell(String.valueOf(totalAmount), PdfPCell.ALIGN_RIGHT, HELVETICA_10_NORMAL_GRAY));
		
		return table;
	}
	
	private PdfPTable getPaymentMethod(PaymentMethod paymentMethod) {
		PdfPTable table = new PdfPTable(1);
		table.setWidthPercentage(100);
		table.setSpacingBefore(5);
		table.setSpacingAfter(30);
        
		table.addCell(getHeaderItemCell("Payment Method", PdfPCell.ALIGN_LEFT, HELVETICA_10_NORMAL_LIGHT_BLUE));
		table.addCell(getServiceCell(String.format("Credit Card: %s ending in %s", paymentMethod.getCardType(), paymentMethod.getLastFour()), PdfPCell.ALIGN_LEFT, HELVETICA_10_NORMAL_GRAY));
        
        return table;
	}
	
	private Chunk getSeparator() {
		LineSeparator separator = new LineSeparator();
        separator.setLineWidth(0.1f);
        return new Chunk(separator);
	}
	
	private PdfPCell getHeaderItemCell(String text, int alignment, Font font) {
		PdfPCell cell = new PdfPCell(new Phrase(text, font));
		cell.setPadding(0);
	    cell.setHorizontalAlignment(alignment);
	    cell.setVerticalAlignment(PdfPCell.ALIGN_JUSTIFIED);
	    cell.setBorder(PdfPCell.BOTTOM);
	    cell.setPaddingBottom(2.0f);
	    return cell;
	}
	
	private PdfPCell getServiceCell(String text, int alignment, Font font) {
		PdfPCell cell = new PdfPCell(new Phrase(text, font));
		cell.setPadding(0);
	    cell.setHorizontalAlignment(alignment);
	    cell.setVerticalAlignment(PdfPCell.ALIGN_JUSTIFIED);
	    cell.setBorder(PdfPCell.NO_BORDER);
	    cell.setPaddingTop(5.0f);
	    cell.setPaddingBottom(5.0f);
	    return cell;
	}
	
	private PdfPCell getCell(String text, int alignment, Font font) {
	    PdfPCell cell = new PdfPCell(new Phrase(text, font));
	    cell.setPadding(0);
	    cell.setHorizontalAlignment(alignment);
	    cell.setVerticalAlignment(PdfPCell.ALIGN_BOTTOM);
	    cell.setBorder(PdfPCell.NO_BORDER);
	    cell.setPaddingBottom(1.0f);
	    return cell;
	}
	
	private PdfPCell getInvoiceCell() {		
	    PdfPCell cell = new PdfPCell(new Phrase("INVOICE", HELVETICA_14_NORMAL_LIGHT_BLUE));
	    cell.setPadding(0);
	    cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
	    cell.setVerticalAlignment(PdfPCell.ALIGN_BOTTOM);
	    cell.setBorder(PdfPCell.NO_BORDER);
	    cell.setPaddingBottom(30.0f);
	    return cell;
	}
	
	private PdfPCell getLogoCell() {		
	    PdfPCell cell = new PdfPCell(new Phrase("nowellpoint", new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD, new BaseColor(237, 122, 40))));
	    cell.setPadding(0);
	    cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
	    cell.setVerticalAlignment(PdfPCell.ALIGN_BOTTOM);
	    cell.setBorder(PdfPCell.NO_BORDER);
	    cell.setPaddingBottom(30.0f);
	    return cell;
	}
	
	private void sendInvoice(String customerId, String email, String name, String invoiceNumber, String base64EncodedContent) {
		Executors.newSingleThreadExecutor().execute(new Runnable() {
			@Override
			public void run() {
				Email from = new Email();
				from.setEmail("billing@nowellpoint.com");
				from.setName("Nowellpoint Billing");
			    
			    Email to = new Email();
			    to.setEmail(email);
			    to.setName(name);
			    
			    Content content = new Content();
			    content.setType("text/html");
			    content.setValue("<html><body>some text here</body></html>");
			    	    
			    Personalization personalization = new Personalization();
			    personalization.addTo(to);
			    personalization.addSubstitution("%name%", name);
			    personalization.addSubstitution("%invoice-link%", String.format("https://localhost:8443/app/account-profile/%s/current-plan", customerId));
			    
			    Attachments attachments = new Attachments();
			    attachments.setContent(base64EncodedContent);
			    attachments.setType("application/pdf");
			    attachments.setFilename(String.format("invoice_%s.pdf", invoiceNumber));
			    attachments.setDisposition("attachment");
			    attachments.setContentId("Invoice");
			    
			    Mail mail = new Mail();
			    mail.setFrom(from);
			    mail.addContent(content);
			    mail.setTemplateId("78e36394-86c3-4e16-be73-a3ed3ddae1a8");
			    mail.addPersonalization(personalization);
			    mail.addAttachments(attachments);
			    
			    SendGrid sendgrid = new SendGrid(System.getProperty(Properties.SENDGRID_API_KEY));
			    
			    Request request = new Request();
			    try {
			    	request.method = Method.POST;
			    	request.endpoint = "mail/send";
			    	request.body = mail.build();
			    	Response response = sendgrid.api(request);
			    	LOG.info("sendInvoiceMessage: " + response.statusCode + " " + response.body);
			    } catch (IOException e) {
			    	LOG.error(e);
			    }
			}
		});	
	}
	
	private String getLabel(String key) {
		return ResourceBundle.getBundle("invoice", locale).getString(key);
	}
}