package dart.handler.handlers;

import dart.handler.ControllerMain;
import dart.handler.interfaces.FileFinder;
import dart.handler.interfaces.ProcessUnhandled;

import java.io.File;
import java.util.ArrayList;

public class AttachmentsHandler implements FileFinder, ProcessUnhandled {
    private final File handledDir = ControllerMain.getHandledDir();

    public void handleAttachments() {
        ArrayList<File> files = new ArrayList<>();
        getFiles(files, handledDir);

        for (File file : files) {
            if (file.getName().endsWith("doc") || file.getName().endsWith("docx")) {
                MSWordHandler msWordParser = new MSWordHandler();
                msWordParser.parseWord(file);
            } else if (file.getName().endsWith("xls") || file.getName().endsWith("xlsx")) {
                ExcelHandler excelParser = new ExcelHandler();
                excelParser.parseExcel(file);
            } else if (file.getName().endsWith("ppt") || file.getName().endsWith("pptx")) {
                PowerPointHandler powerPointParser = new PowerPointHandler();
                powerPointParser.parsePpt(file);
            } else if (file.getName().endsWith("pdf")) {
                PDFHandler pdfParser = new PDFHandler();
                pdfParser.parsePDF(file);
            } else if (file.getName().endsWith("txt")) {
                continue;
            } else {
                processUnhandledFile(file);
            }
        }
    }
}
