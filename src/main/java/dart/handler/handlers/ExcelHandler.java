package dart.handler.handlers;

import dart.handler.interfaces.ProcessUnhandled;
import org.apache.commons.io.FileUtils;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.tika.Tika;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;

public class ExcelHandler implements ProcessUnhandled {


    public void parseExcel(File file) {
        Tika tika = new Tika();
        String fileName = file.getName();
        try {
            if (tika.detect(file).contains("ms-excel")) {
                String outputFile = file.getParent() + File.separator + fileName.substring(0, fileName.length() - 4) + ".txt";
                try (FileInputStream fis = new FileInputStream(file)) {
                    Workbook workbook = new HSSFWorkbook(fis);
                    handle(workbook, outputFile);
                } catch (EncryptedDocumentException e) {
                    processUnhandledFile(file);
                }
            } else {
                String outputFile = file.getParent() + File.separator + fileName.substring(0, fileName.length() - 5) + ".txt";
                try (FileInputStream fis = new FileInputStream(file)) {
                    Workbook workbook = new XSSFWorkbook(fis);
                    handle(workbook, outputFile);
                } catch (EncryptedDocumentException e) {
                    processUnhandledFile(file);
                }
            }
            FileUtils.deleteQuietly(file);
        } catch (IOException e) {
            processUnhandledFile(file);
        }

    }

    private void handle(Workbook workbook, String outputFile) throws IOException {
        Sheet firstSheet = workbook.getSheetAt(0);
        Iterator<Row> iterator = firstSheet.iterator();
        FileWriter fileWriter = new FileWriter(outputFile);
        while (iterator.hasNext()) {
            Row nextRow = iterator.next();
            Iterator<Cell> cellIterator = nextRow.cellIterator();
            while (cellIterator.hasNext()) {
                Cell cell = cellIterator.next();
                switch (cell.getCellType()) {
                    case STRING:
                        fileWriter.write(cell.getStringCellValue() + ", ");
                        break;
                    case BOOLEAN:
                        fileWriter.write(cell.getBooleanCellValue() + ", ");
                        break;
                    case NUMERIC:
                        fileWriter.write(cell.getNumericCellValue() + ", ");
                        break;
                    case FORMULA:
                        fileWriter.write(cell.getCellFormula() + ", ");
                        break;
                    default:
                        fileWriter.write(cell.getStringCellValue() + ", ");
                }
            }
            fileWriter.write("\n");
        }
        workbook.close();
        fileWriter.flush();
        fileWriter.close();
    }
}
