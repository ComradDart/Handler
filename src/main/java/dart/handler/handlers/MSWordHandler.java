package dart.handler.handlers;

import dart.handler.interfaces.ProcessUnhandled;
import org.apache.commons.io.FileUtils;
import org.apache.poi.extractor.POITextExtractor;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.tika.Tika;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;

public class MSWordHandler implements ProcessUnhandled {

    public void parseWord(File file) {
        Tika tika = new Tika();
        String fileName = file.getName();
        try {
            POITextExtractor extractor;

            if (tika.detect(file).contains("msword")) {
                //Handling doc
                String outputFile = file.getParent() + File.separator + fileName.substring(0, fileName.length() - 4) + ".txt";
                try (FileInputStream fis = new FileInputStream(file)) {
                    HWPFDocument doc = new HWPFDocument(fis);
                    extractor = new WordExtractor(doc);
                    FileWriter fileWriter = new FileWriter(outputFile);
                    fileWriter.write(extractor.getText());
                    fileWriter.flush();
                    fileWriter.close();
                } catch (Exception e) {
                    processUnhandledFile(file);
                }

            } else {
                //Handling docx
                String outputFile = file.getParent() + File.separator + fileName.substring(0, fileName.length() - 5) + ".txt";
                try (FileInputStream fis = new FileInputStream(file)) {
                    XWPFDocument doc = new XWPFDocument(fis);
                    extractor = new XWPFWordExtractor(doc);
                    FileWriter fileWriter = new FileWriter(outputFile);
                    fileWriter.write(extractor.getText());
                    fileWriter.flush();
                    fileWriter.close();
                } catch (Exception e) {
                    processUnhandledFile(file);
                }
            }
            FileUtils.deleteQuietly(file);
        } catch (IOException e) {
            processUnhandledFile(file);
        }
    }

}
