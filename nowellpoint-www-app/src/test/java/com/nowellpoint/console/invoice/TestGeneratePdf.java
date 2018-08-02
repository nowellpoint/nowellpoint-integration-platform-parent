package com.nowellpoint.console.invoice;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Optional;

import org.bson.types.ObjectId;
import org.junit.Test;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;

import com.braintreegateway.BraintreeGateway;
import com.braintreegateway.Environment;
import com.braintreegateway.Plan;
import com.braintreegateway.Subscription;
import com.braintreegateway.Transaction;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.nowellpoint.console.entity.Organization;
import com.nowellpoint.console.service.OrganizationService;
import com.nowellpoint.www.app.util.EnvironmentVariables;

public class TestGeneratePdf {
	
	@Test
	public void testGeneratePdf() {
		
		MongoClientURI mongoClientUri = new MongoClientURI(String.format("mongodb://%s", EnvironmentVariables.getMongoClientUri()));
		MongoClient mongoClient = new MongoClient(mongoClientUri);
        
        final Morphia morphia = new Morphia();
        
        morphia.map(Organization.class);

        Datastore datastore = morphia.createDatastore(mongoClient, mongoClientUri.getDatabase());
		
		Organization organization = datastore.get(Organization.class, new ObjectId("59d592ce5e7a9bb2231a87ed"));
		
		BraintreeGateway gateway = new BraintreeGateway(
				Environment.parseEnvironment(System.getenv("BRAINTREE_ENVIRONMENT")),
				System.getenv("BRAINTREE_MERCHANT_ID"),
				System.getenv("BRAINTREE_PUBLIC_KEY"),
				System.getenv("BRAINTREE_PRIVATE_KEY")
		);
		
		gateway.clientToken().generate();
		
		Subscription subscription = gateway.subscription().find(organization.getSubscription().getNumber());
		
		Optional<Transaction> optional = subscription.getTransactions()
				.stream()
				.filter(t -> t.getId().equals("ennqjn1q"))
				.findFirst();
		
		if (optional.isPresent()) {
			
			Transaction transaction = optional.get();
			
			Plan plan = gateway.plan()
					.all()
					.stream()
					.filter(p -> p.getId().equals(transaction.getPlanId()))
					.findFirst()
					.get();
		
			Document document = new Document();
			
			try {
				
				OutputStream output = new FileOutputStream(System.getProperty("user.home") + "/invoice_test.pdf");
				
				PdfWriter.getInstance(document, output);
				
				document.open();
				
				document.setMargins(75, 75, 75, 10);
//				document.addTitle(getLabel(InvoiceLabels.INVOICE_TITLE));
//				document.addAuthor(getLabel(InvoiceLabels.INVOICE_AUTHOR));
//				document.addSubject(String.format(getLabel(InvoiceLabels.INVOICE_SUBJECT), invoice.getPayee().getCompanyName()));
				document.addCreator(OrganizationService.class.getName());
				 
				document.add(getHeader(transaction));
				document.add(new Chunk("Invoice", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, BaseColor.BLACK)));
				document.add(getPayer(transaction));
				document.add(Chunk.NEWLINE);
				document.add(getPlan(plan, transaction));
				document.add(getPaymentMethod(transaction));
				
				document.close();  
				
			} catch (DocumentException | FileNotFoundException e) {
				e.printStackTrace();
			}
		}
		
		mongoClient.close();
	}
	
	private PdfPTable getHeader(Transaction transaction) {
		SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-dd");
		PdfPTable table = new PdfPTable(2);
		table.setWidthPercentage(100);
		table.setSpacingAfter(36f);
		table.addCell(getCell("NOWELLPOINT, LLC", PdfPCell.ALIGN_LEFT, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.BLACK)));
		table.addCell(getCell("Account Number:  " + String.format("%s", transaction.getCustomer().getId()), PdfPCell.ALIGN_RIGHT, FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.BLACK)));
		table.addCell(getCell("129 S. Bloodworth Street", PdfPCell.ALIGN_LEFT, FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.BLACK)));
		table.addCell(getCell("Invoice Number:  " + String.format("%s", transaction.getId().toUpperCase()), PdfPCell.ALIGN_RIGHT, FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.BLACK)));
		table.addCell(getCell("Raleigh, NC 27601", PdfPCell.ALIGN_LEFT, FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.BLACK)));
		table.addCell(getCell("Invoice Date:  " + String.format("%s", sdf.format(transaction.getCreatedAt().getTime())), PdfPCell.ALIGN_RIGHT, FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.BLACK)));
		table.addCell(getCell("United States", PdfPCell.ALIGN_LEFT, FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.BLACK)));
		table.addCell(getCell(" ", PdfPCell.ALIGN_RIGHT, FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.BLACK)));
		table.addCell(getCell(" ", PdfPCell.ALIGN_LEFT, FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.BLACK)));
		table.addCell(getCell(" ", PdfPCell.ALIGN_RIGHT, FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.BLACK)));
		table.addCell(getCell("Tax ID: 47-5575435", PdfPCell.ALIGN_LEFT, FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.BLACK)));
		table.addCell(getCell(" ", PdfPCell.ALIGN_RIGHT, FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.BLACK)));
		return table;
	}
	
	private PdfPTable getPayer(Transaction transaction) {
		PdfPTable table = new PdfPTable(1);
		table.setWidthPercentage(100);
		table.setSpacingBefore(18f);
		table.setSpacingAfter(36f);
		table.addCell(getCell(transaction.getCustomer().getCompany().toUpperCase(), PdfPCell.ALIGN_LEFT, FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.BLACK)));
		table.addCell(getCell(transaction.getCustomer().getFirstName() + " " + transaction.getCustomer().getLastName(), PdfPCell.ALIGN_LEFT, FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.BLACK)));
		table.addCell(getCell(transaction.getBillingAddress().getStreetAddress(), PdfPCell.ALIGN_LEFT, FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.BLACK)));
		table.addCell(getCell(transaction.getBillingAddress().getLocality() + ", " + transaction.getBillingAddress().getRegion() + " " + transaction.getBillingAddress().getPostalCode(), PdfPCell.ALIGN_LEFT, FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.BLACK)));
		table.addCell(getCell(transaction.getBillingAddress().getCountryName(), PdfPCell.ALIGN_LEFT, FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.BLACK)));
		return table;
	}
	
	private PdfPTable getPlan(Plan plan, Transaction transaction) {	
		SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, YYYY");
		PdfPTable table = new PdfPTable(3);
		table.setWidthPercentage(100);
		table.setSpacingAfter(36f);
		table.addCell(getHeaderCell("Plan", PdfPCell.ALIGN_LEFT, FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.BLACK)));
		table.addCell(getHeaderCell("Billing Period", PdfPCell.ALIGN_LEFT, FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.BLACK)));
		table.addCell(getHeaderCell("Price", PdfPCell.ALIGN_RIGHT, FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.BLACK)));
		table.addCell(getPlanCell(plan.getName(), PdfPCell.ALIGN_LEFT, FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.BLACK)));
		table.addCell(getPlanCell(sdf.format(transaction.getSubscription().getBillingPeriodStartDate().getTime()) + " - " + sdf.format(transaction.getSubscription().getBillingPeriodEndDate().getTime()), PdfPCell.ALIGN_LEFT, FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.BLACK)));
		table.addCell(getPlanCell(transaction.getCurrencyIsoCode() + " " + transaction.getAmount(), PdfPCell.ALIGN_RIGHT, FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.BLACK)));
		return table;
	}
	
	private Chunk getPaymentMethod(Transaction transaction) {	
		Chunk chunk = new Chunk("Payment Method: " + transaction.getCreditCard().getCardType() + " " + transaction.getCreditCard().getLast4(), FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.BLACK));
		return chunk;
	}
	
	private PdfPCell getCell(String text, int alignment, Font font) {
	    PdfPCell cell = new PdfPCell(new Phrase(text, font));
	    cell.setPadding(0);
	    cell.setPaddingBottom(2f);
	    cell.setHorizontalAlignment(alignment);
	    cell.setBorder(PdfPCell.NO_BORDER);
	    return cell;
	}
	
	private PdfPCell getHeaderCell(String text, int alignment, Font font) {
	    PdfPCell cell = new PdfPCell(new Phrase(text, font));
	    cell.setPadding(2f);
	    cell.setPaddingLeft(5f);
	    cell.setPaddingRight(5f);
	    cell.setPaddingBottom(13f);
	    cell.setPaddingTop(13f);
	    cell.setHorizontalAlignment(alignment);
	    cell.setBorder(PdfPCell.NO_BORDER);
	    cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
	    return cell;
	}
	
	private PdfPCell getPlanCell(String text, int alignment, Font font) {
	    PdfPCell cell = new PdfPCell(new Phrase(text, font));
	    cell.setPadding(2f);
	    cell.setPaddingBottom(5f);
	    cell.setPaddingLeft(5f);
	    cell.setPaddingRight(5f);
	    cell.setHorizontalAlignment(alignment);
	    cell.setBorder(PdfPCell.BOTTOM);
	    return cell;
	}
}