package dart.handler.handlers;

import dart.handler.interfaces.ProcessUnhandled;
import org.apache.commons.io.FileUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class PDFHandler implements ProcessUnhandled {

    public void parsePDF(File file) {
        String fileName = file.getName();
        String outputFile = file.getParent() + File.separator + fileName.substring(0, fileName.length() - 4) + ".txt";
        try (FileWriter writer = new FileWriter(outputFile)) {
            PDDocument document = PDDocument.load(file);
            if (!document.isEncrypted()) {
                PDFTextStripper pdfTextStripper = new PDFTextStripper();
                String text = pdfTextStripper.getText(document);
                writer.write(text);

                document.close();
                writer.flush();

                //Checking if pdf contains only pics
                File result = new File(outputFile);
                if (result.length() < 5) {
                    processUnhandledFile(file);
                    FileUtils.deleteQuietly(new File(outputFile));
                }

            } else {
                processUnhandledFile(file);
            }
        } catch (IOException e) {
            processUnhandledFile(file);
        }
        FileUtils.deleteQuietly(file);
    }
}
