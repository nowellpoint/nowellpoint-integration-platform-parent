package com.nowellpoint.pdfbox;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Test;

import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;

public class TestGenerateDocument {
	
	private Font font = new Font(Font.FontFamily.HELVETICA, 8);
    private Font gray = new Font(Font.FontFamily.HELVETICA, 8);
    private Font grayBold = new Font(Font.FontFamily.HELVETICA, 8, Font.BOLD);
	
	@Test
	public void testGeneratePDF() {
        
        Document document = new Document();
        
        try {
			PdfWriter writer = PdfWriter.getInstance(document,
			        new FileOutputStream("/Users/jherson/workspace/nowellpoint-integration-platform-parent/nowellpoint-aws-test/Invoice_8ttpezf0.pdf"));
			
			document.setMargins(75, 75, 75, 10);
			document.open();
	        
			font.setColor(27, 150, 254);
			gray.setColor(79,79,79);
			gray.setColor(79,79,79);
			
	        document.add(addHeaderTable());
	        
	        PdfPTable itemTable = new PdfPTable(4);
	        itemTable.setWidthPercentage(100);
	        itemTable.setSpacingBefore(5);
	        itemTable.setSpacingAfter(10);
	        itemTable.setWidths(new int[]{3, 1, 1, 1});
	        
	        itemTable.addCell(getHeaderItemCell("Plan", PdfPCell.ALIGN_LEFT, font));
	        itemTable.addCell(getHeaderItemCell("Unit", PdfPCell.ALIGN_RIGHT, font));
	        itemTable.addCell(getHeaderItemCell("Quantity", PdfPCell.ALIGN_RIGHT, font));
	        itemTable.addCell(getHeaderItemCell("Amount", PdfPCell.ALIGN_RIGHT, font));
	        
	        itemTable.addCell(getItemCell("Basic (Billing Period: June 1, 2017 - June 30, 2017)", PdfPCell.ALIGN_LEFT, gray));
	        itemTable.addCell(getItemCell("7.00", PdfPCell.ALIGN_RIGHT, gray));
	        itemTable.addCell(getItemCell("1", PdfPCell.ALIGN_RIGHT, gray));
	        itemTable.addCell(getItemCell("7.00", PdfPCell.ALIGN_RIGHT, gray));
	        
	        itemTable.addCell(getItemCell("Total", PdfPCell.ALIGN_LEFT, gray));
	        itemTable.addCell(getItemCell("", PdfPCell.ALIGN_RIGHT, gray));
	        itemTable.addCell(getItemCell("", PdfPCell.ALIGN_RIGHT, gray));
	        itemTable.addCell(getItemCell("7.00", PdfPCell.ALIGN_RIGHT, gray));
	        
	        document.add(itemTable);
	        
	        PdfPTable paymentTable = new PdfPTable(1);
	        paymentTable.setWidthPercentage(100);
	        paymentTable.setSpacingBefore(5);
	        paymentTable.setSpacingAfter(30);
	        
	        paymentTable.addCell(getHeaderItemCell("Payment Method", PdfPCell.ALIGN_LEFT, font));
	        
	        paymentTable.addCell(getItemCell("Credit Card: Visa ending in 4444", PdfPCell.ALIGN_LEFT, gray));
	        
	        document.add(paymentTable);

	        LineSeparator separator = new LineSeparator();
	        separator.setLineWidth(0.1f);
	        
	        Chunk linebreak = new Chunk(separator);
	        
	        document.add(linebreak);
	        
		} catch (FileNotFoundException | DocumentException e) {
			e.printStackTrace();
		}

        document.close();
	}
	
	private PdfPTable addHeaderTable() {
		PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setSpacingAfter(30);
        table.addCell(getLogoCell());
        table.addCell(getInvoiceCell());
        
        table.addCell(getCell("Nowellpoint LLC", PdfPCell.ALIGN_LEFT, font));
        table.addCell(getCell("Issue Date", PdfPCell.ALIGN_RIGHT, font));
        
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM d, yyyy");
        
        table.addCell(getCell("129 S. Bloodworth Street", PdfPCell.ALIGN_LEFT, gray));
        table.addCell(getCell(sdf.format(new Date()), PdfPCell.ALIGN_RIGHT, gray));
        
        table.addCell(getCell("Raleigh, NC 27601", PdfPCell.ALIGN_LEFT, gray));
        table.addCell(getCell("", PdfPCell.ALIGN_RIGHT, gray));
        
        table.addCell(getCell("United States", PdfPCell.ALIGN_LEFT, gray));
        table.addCell(getCell("", PdfPCell.ALIGN_RIGHT, font));
        
        table.addCell(getCell("1-888-721-6440", PdfPCell.ALIGN_LEFT, gray));
        table.addCell(getCell("Invoice No", PdfPCell.ALIGN_RIGHT, font));
        
        PdfPCell c1 = getCell("www.nowellpoint.com", PdfPCell.ALIGN_LEFT, gray);
        c1.setPaddingBottom(20.0f);
        
        PdfPCell c2 = getCell("8ttpezf0", PdfPCell.ALIGN_RIGHT, gray);
        c2.setPaddingBottom(20.0f);
        
        table.addCell(c1);
        table.addCell(c2);
        
        table.addCell(getCell("Bill To", PdfPCell.ALIGN_LEFT, font));
        table.addCell(getCell("", PdfPCell.ALIGN_RIGHT, font));
        
        table.addCell(getCell("Patheon", PdfPCell.ALIGN_LEFT, grayBold));
        table.addCell(getCell("", PdfPCell.ALIGN_RIGHT, gray));
        
        table.addCell(getCell("ATTN: Deborah Lancaster", PdfPCell.ALIGN_LEFT, gray));
        table.addCell(getCell("", PdfPCell.ALIGN_RIGHT, gray));
        
        table.addCell(getCell("4815 Emperor Blvd", PdfPCell.ALIGN_LEFT, gray));
        table.addCell(getCell("", PdfPCell.ALIGN_RIGHT, gray));
        
        table.addCell(getCell("Suite 300", PdfPCell.ALIGN_LEFT, gray));
        table.addCell(getCell("", PdfPCell.ALIGN_RIGHT, gray));
        
        table.addCell(getCell("Durham, NC 27703", PdfPCell.ALIGN_LEFT, gray));
        table.addCell(getCell("", PdfPCell.ALIGN_RIGHT, gray));
        
        table.addCell(getCell("United States", PdfPCell.ALIGN_LEFT, gray));
        table.addCell(getCell("", PdfPCell.ALIGN_RIGHT, gray));
        
        return table;
	}
	
	public PdfPCell getHeaderItemCell(String text, int alignment, Font font) {
		
		PdfPCell cell = new PdfPCell(new Phrase(text, font));
		cell.setPadding(0);
	    cell.setHorizontalAlignment(alignment);
	    cell.setVerticalAlignment(PdfPCell.ALIGN_JUSTIFIED);
	    cell.setBorder(PdfPCell.BOTTOM);
	    cell.setPaddingBottom(2.0f);
	    return cell;
	}
	
	public PdfPCell getItemCell(String text, int alignment, Font font) {
		
		PdfPCell cell = new PdfPCell(new Phrase(text, font));
		cell.setPadding(0);
	    cell.setHorizontalAlignment(alignment);
	    cell.setVerticalAlignment(PdfPCell.ALIGN_JUSTIFIED);
	    cell.setBorder(PdfPCell.NO_BORDER);
	    cell.setPaddingTop(5.0f);
	    cell.setPaddingBottom(5.0f);
	    return cell;
	}
	
	public PdfPCell getCell(String text, int alignment, Font font) {
		
	    PdfPCell cell = new PdfPCell(new Phrase(text, font));
	    cell.setPadding(0);
	    cell.setHorizontalAlignment(alignment);
	    cell.setVerticalAlignment(PdfPCell.ALIGN_BOTTOM);
	    cell.setBorder(PdfPCell.NO_BORDER);
	    cell.setPaddingBottom(1.0f);
	    return cell;
	}
	
	public PdfPCell getInvoiceCell() {
		
		Font font1 = new Font(Font.FontFamily.HELVETICA, 14);
		font1.setColor(27, 150, 254);
		
	    PdfPCell cell = new PdfPCell(new Phrase("INVOICE", font1));
	    cell.setPadding(0);
	    cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
	    cell.setVerticalAlignment(PdfPCell.ALIGN_BOTTOM);
	    cell.setBorder(PdfPCell.NO_BORDER);
	    cell.setPaddingBottom(30.0f);
	    return cell;
	}
	
	public PdfPCell getLogoCell() {
		Font font1 = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
		font1.setColor(237, 122, 40);
		
	    PdfPCell cell = new PdfPCell(new Phrase("nowellpoint", font1));
	    cell.setPadding(0);
	    cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
	    cell.setVerticalAlignment(PdfPCell.ALIGN_BOTTOM);
	    cell.setBorder(PdfPCell.NO_BORDER);
	    cell.setPaddingBottom(30.0f);
	    return cell;
	}
}