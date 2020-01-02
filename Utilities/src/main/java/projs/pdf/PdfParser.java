package projs.pdf;

import java.io.File;
import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

public class PdfParser{

   public static void main(String args[]) throws IOException {
      //Loading an existing document
      File file = new File("C:\\Users\\siddharth.s\\Downloads\\The India Leave Policy.pdf");
      PDDocument document = PDDocument.load(file);

      //Instantiate PDFTextStripper class
      PDFTextStripper pdfStripper = new PDFTextStripper();

      //Retrieving text from PDF document
      String text = pdfStripper.getText(document);
      System.out.println(text);

      //Closing the document
      document.close();
   }
}