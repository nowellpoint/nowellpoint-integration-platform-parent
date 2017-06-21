package com.nowellpoint.pdfbox;

import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.junit.Test;

public class TestGenerateDocument {
	
	@Test
	public void testGeneratePDF() {
		PDDocument document = new PDDocument();

		PDPage page = new PDPage();
		document.addPage( page );
		
		PDFont font = PDType1Font.HELVETICA;
		
		try {
			
			PDPageContentStream contents = new PDPageContentStream(document, page);
			contents.beginText();
			contents.setFont(font, 12);
			contents.newLineAtOffset(100, 700);
			contents.showText("Scorecard");
			contents.endText();
			contents.close();
		
			document.save("BlankPage.pdf");
			document.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}