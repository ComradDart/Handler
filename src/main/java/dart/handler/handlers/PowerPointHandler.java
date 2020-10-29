package dart.handler.handlers;

import dart.handler.interfaces.ProcessUnhandled;
import org.apache.commons.io.FileUtils;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.hslf.usermodel.HSLFShape;
import org.apache.poi.hslf.usermodel.HSLFSlideShow;
import org.apache.poi.hslf.usermodel.HSLFTextParagraph;
import org.apache.poi.sl.extractor.SlideShowExtractor;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFShape;
import org.apache.poi.xslf.usermodel.XSLFTextParagraph;
import org.apache.tika.Tika;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;

public class PowerPointHandler implements ProcessUnhandled {

    public void parsePpt(File file) {
        Tika tika = new Tika();
        String fileName = file.getName();
        try {
            if (tika.detect(file).contains("ms-powerpoint")) {
                handlePPT(file,fileName);
            } else {
                handlePPTX(file, fileName);

            }
        } catch (IOException e) {
            processUnhandledFile(file);
        }
        FileUtils.deleteQuietly(file);
    }

    private void handlePPT(File file, String fileName) throws IOException {

        try (FileInputStream fis = new FileInputStream(file)) {
            String outputFile = file.getParent() + File.separator + fileName.substring(0, fileName.length() - 4) + ".txt";
            HSLFSlideShow ppt = new HSLFSlideShow(fis);
            SlideShowExtractor<HSLFShape, HSLFTextParagraph> slideShowExtractor = new SlideShowExtractor<>(ppt);
            slideShowExtractor.setCommentsByDefault(true);
            slideShowExtractor.setMasterByDefault(true);
            slideShowExtractor.setNotesByDefault(true);

            FileWriter fileWriter = new FileWriter(outputFile);
            String text = slideShowExtractor.getText();
            fileWriter.write(text);

            fileWriter.flush();
            fileWriter.close();
        } catch (EncryptedDocumentException e) {
            processUnhandledFile(file);
        }
    }

    private void handlePPTX(File file, String fileName) throws IOException {

        try (FileInputStream fis = new FileInputStream(file)) {
            String outputFile = file.getParent() + File.separator + fileName.substring(0, fileName.length() - 5) + ".txt";
            XMLSlideShow ppt = new XMLSlideShow(fis);
            SlideShowExtractor<XSLFShape, XSLFTextParagraph> slideShowExtractor = new SlideShowExtractor<>(ppt);
            slideShowExtractor.setCommentsByDefault(true);
            slideShowExtractor.setMasterByDefault(true);
            slideShowExtractor.setNotesByDefault(true);

            FileWriter fileWriter = new FileWriter(outputFile);
            String text = slideShowExtractor.getText();
            fileWriter.write(text);

            fileWriter.flush();
            fileWriter.close();
        } catch (EncryptedDocumentException e) {
            processUnhandledFile(file);
        }
    }
}
