package com.nowellpoint.pdf;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

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
import com.nowellpoint.client.ClientCredentialsAuthenticator;
import com.nowellpoint.client.NowellpointClient;
import com.nowellpoint.client.auth.Authenticators;
import com.nowellpoint.client.auth.OauthRequests;
import com.nowellpoint.client.auth.RevokeTokenRequest;
import com.nowellpoint.client.model.AccountProfile;
import com.nowellpoint.client.model.Identity;
import com.nowellpoint.client.model.Token;

public class TestGenerateDocument {
	
	private static Token token;
	private static AccountProfile accountProfile;
	
	private Font HELVETICA_10_NORMAL_LIGHT_BLUE = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL, new BaseColor(27, 150, 254));
    private Font HELVETICA_10_NORMAL_GRAY = new Font(Font.FontFamily.HELVETICA, 10.0f, Font.NORMAL, new BaseColor(79,79,79));
    private Font HELVETICA_10_BOLD_GRAY = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD, new BaseColor(79,79,79));
    
    @BeforeClass
	public static void authenticate() {
		token = new ClientCredentialsAuthenticator().authenticate();
		
		Identity identity = NowellpointClient.defaultClient(token)
				.identity()
				.get(token.getId());
		
		accountProfile = NowellpointClient.defaultClient(token)
				.accountProfile()
				.get(identity.getId());
	}
    
    @AfterClass
	public static void logout() {
		RevokeTokenRequest revokeTokenRequest = OauthRequests.REVOKE_TOKEN_REQUEST.builder()
				.setToken(token)
				.build();
		
		Authenticators.REVOKE_TOKEN_INVALIDATOR.revoke(revokeTokenRequest);
	}
	
	@Test
	public void testGeneratePDF() {
        
        Document document = new Document();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        
        try {
        	
        	PdfWriter.getInstance(document, baos);
        	
			document.setMargins(75, 75, 75, 10);
			document.open();
			
			document.add(getHeader());
	        document.add(getCompany());
	        document.add(getPayee());
	        document.add(getBillingPeriod());
	        document.add(getServicesList());
	        document.add(getPaymentMethod());
	        document.add(getFooter());
	        
		} catch (DocumentException e) {
			e.printStackTrace();
		}

        document.close();
        
        byte[] bytes = baos.toByteArray();
		
		ObjectMetadata objectMetadata = new ObjectMetadata();
		objectMetadata.setContentLength(bytes.length);
		
		AmazonS3 s3client = AmazonS3ClientBuilder.defaultClient();
		
		s3client.putObject(new PutObjectRequest(
				"nowellpoint-invoices",
				"test",
				new ByteArrayInputStream(bytes),
				objectMetadata));
		
		System.out.println(s3client.getUrl("nowellpoint-invoices", "test"));
	}
	
	private PdfPTable getBillingPeriod() {
		PdfPCell cell = new PdfPCell(new Phrase("Billing Period: June 1, 2017 - June 30, 2017", HELVETICA_10_NORMAL_LIGHT_BLUE));
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
	
	private PdfPTable getPayee() {
		PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setSpacingAfter(30);
        
        table.addCell(getCell("Bill To", PdfPCell.ALIGN_LEFT, HELVETICA_10_NORMAL_LIGHT_BLUE));
        table.addCell(getCell("", PdfPCell.ALIGN_RIGHT, HELVETICA_10_NORMAL_LIGHT_BLUE));
        
        table.addCell(getCell(accountProfile.getCompany(), PdfPCell.ALIGN_LEFT, HELVETICA_10_BOLD_GRAY));
        table.addCell(getCell("", PdfPCell.ALIGN_RIGHT, HELVETICA_10_NORMAL_GRAY));
        
        table.addCell(getCell(String.format("ATTN: %s", accountProfile.getName()), PdfPCell.ALIGN_LEFT, HELVETICA_10_NORMAL_GRAY));
        table.addCell(getCell("", PdfPCell.ALIGN_RIGHT, HELVETICA_10_NORMAL_GRAY));
        
        table.addCell(getCell(accountProfile.getAddress().getStreet(), PdfPCell.ALIGN_LEFT, HELVETICA_10_NORMAL_GRAY));
        table.addCell(getCell("", PdfPCell.ALIGN_RIGHT, HELVETICA_10_NORMAL_GRAY));
        
        table.addCell(getCell("Suite 300", PdfPCell.ALIGN_LEFT, HELVETICA_10_NORMAL_GRAY));
        table.addCell(getCell("", PdfPCell.ALIGN_RIGHT, HELVETICA_10_NORMAL_GRAY));
        
        table.addCell(getCell(accountProfile.getAddress().getCity(), PdfPCell.ALIGN_LEFT, HELVETICA_10_NORMAL_GRAY));
        table.addCell(getCell("", PdfPCell.ALIGN_RIGHT, HELVETICA_10_NORMAL_GRAY));
        
        table.addCell(getCell(accountProfile.getAddress().getCountry(), PdfPCell.ALIGN_LEFT, HELVETICA_10_NORMAL_GRAY));
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
	
	private PdfPTable getCompany() {
		PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setSpacingAfter(30);
        
        table.addCell(getCell("Nowellpoint LLC", PdfPCell.ALIGN_LEFT, HELVETICA_10_NORMAL_LIGHT_BLUE));
        table.addCell(getCell("Issue Date", PdfPCell.ALIGN_RIGHT, HELVETICA_10_NORMAL_LIGHT_BLUE));
        
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM d, yyyy");
        
        table.addCell(getCell("129 S. Bloodworth Street", PdfPCell.ALIGN_LEFT, HELVETICA_10_NORMAL_GRAY));
        table.addCell(getCell(sdf.format(new Date()), PdfPCell.ALIGN_RIGHT, HELVETICA_10_NORMAL_GRAY));
        
        table.addCell(getCell("Raleigh, NC 27601", PdfPCell.ALIGN_LEFT, HELVETICA_10_NORMAL_GRAY));
        table.addCell(getCell("", PdfPCell.ALIGN_RIGHT, HELVETICA_10_NORMAL_GRAY));
        
        table.addCell(getCell("United States", PdfPCell.ALIGN_LEFT, HELVETICA_10_NORMAL_GRAY));
        table.addCell(getCell("", PdfPCell.ALIGN_RIGHT, HELVETICA_10_NORMAL_LIGHT_BLUE));
        
        table.addCell(getCell("1-888-721-6440", PdfPCell.ALIGN_LEFT, HELVETICA_10_NORMAL_GRAY));
        table.addCell(getCell("Invoice No", PdfPCell.ALIGN_RIGHT, HELVETICA_10_NORMAL_LIGHT_BLUE));
        
        table.addCell(getCell("www.nowellpoint.com", PdfPCell.ALIGN_LEFT, HELVETICA_10_NORMAL_GRAY));
        table.addCell(getCell("8ttpezf0", PdfPCell.ALIGN_RIGHT, HELVETICA_10_NORMAL_GRAY));
        
        return table;
	}
	
	private PdfPTable getServicesList() throws DocumentException {
		PdfPTable table = new PdfPTable(4);
		table.setWidthPercentage(100);
		table.setSpacingBefore(5);
		table.setSpacingAfter(10);
		table.setWidths(new int[]{3, 1, 1, 1});
        
		table.addCell(getHeaderItemCell("Plan", PdfPCell.ALIGN_LEFT, HELVETICA_10_NORMAL_LIGHT_BLUE));
		table.addCell(getHeaderItemCell("Unit", PdfPCell.ALIGN_RIGHT, HELVETICA_10_NORMAL_LIGHT_BLUE));
		table.addCell(getHeaderItemCell("Quantity", PdfPCell.ALIGN_RIGHT, HELVETICA_10_NORMAL_LIGHT_BLUE));
		table.addCell(getHeaderItemCell("Amount", PdfPCell.ALIGN_RIGHT, HELVETICA_10_NORMAL_LIGHT_BLUE));
        
		table.addCell(getItemCell("Basic", PdfPCell.ALIGN_LEFT, HELVETICA_10_NORMAL_GRAY));
		table.addCell(getItemCell("7.00", PdfPCell.ALIGN_RIGHT, HELVETICA_10_NORMAL_GRAY));
		table.addCell(getItemCell("1", PdfPCell.ALIGN_RIGHT, HELVETICA_10_NORMAL_GRAY));
		table.addCell(getItemCell("7.00", PdfPCell.ALIGN_RIGHT, HELVETICA_10_NORMAL_GRAY));
        
		table.addCell(getItemCell("Total", PdfPCell.ALIGN_LEFT, HELVETICA_10_NORMAL_GRAY));
		table.addCell(getItemCell("", PdfPCell.ALIGN_RIGHT, HELVETICA_10_NORMAL_GRAY));
		table.addCell(getItemCell("", PdfPCell.ALIGN_RIGHT, HELVETICA_10_NORMAL_GRAY));
		table.addCell(getItemCell("7.00", PdfPCell.ALIGN_RIGHT, HELVETICA_10_NORMAL_GRAY));
		
		return table;
	}
	
	private PdfPTable getPaymentMethod() {
		PdfPTable table = new PdfPTable(1);
		table.setWidthPercentage(100);
		table.setSpacingBefore(5);
		table.setSpacingAfter(30);
        
		table.addCell(getHeaderItemCell("Payment Method", PdfPCell.ALIGN_LEFT, HELVETICA_10_NORMAL_LIGHT_BLUE));
		table.addCell(getItemCell("Credit Card: Visa ending in 4444", PdfPCell.ALIGN_LEFT, HELVETICA_10_NORMAL_GRAY));
        
        return table;
	}
	
	private Chunk getFooter() {
		LineSeparator separator = new LineSeparator();
        separator.setLineWidth(0.1f);
        
        Chunk linebreak = new Chunk(separator);
        
        return linebreak;
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
	
	private PdfPCell getItemCell(String text, int alignment, Font font) {
		
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
		
		Font font = new Font(Font.FontFamily.HELVETICA, 14, Font.NORMAL, new BaseColor(27, 150, 254));
		
	    PdfPCell cell = new PdfPCell(new Phrase("INVOICE", font));
	    cell.setPadding(0);
	    cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
	    cell.setVerticalAlignment(PdfPCell.ALIGN_BOTTOM);
	    cell.setBorder(PdfPCell.NO_BORDER);
	    cell.setPaddingBottom(30.0f);
	    return cell;
	}
	
	private PdfPCell getLogoCell() {
		Font font = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD, new BaseColor(237, 122, 40));
		
	    PdfPCell cell = new PdfPCell(new Phrase("nowellpoint", font));
	    cell.setPadding(0);
	    cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
	    cell.setVerticalAlignment(PdfPCell.ALIGN_BOTTOM);
	    cell.setBorder(PdfPCell.NO_BORDER);
	    cell.setPaddingBottom(30.0f);
	    return cell;
	}
}